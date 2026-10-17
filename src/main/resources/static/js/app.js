/**
 * SimUPI - Core Global Application Utilities & State Controller
 * Supports responsive desktop & mobile execution and automatic API endpoint discovery.
 */

const SimUPI = {
    /**
     * Resolves API base path dynamically.
     * Works seamlessly whether opened directly via http://localhost:8080, an external IP, or file://
     */
    get API_BASE() {
        if (window.location.protocol === 'file:') {
            return 'http://localhost:8080/api';
        }
        if (window.location.port !== '8080' && window.location.hostname === 'localhost') {
            return 'http://localhost:8080/api';
        }
        return '/api';
    },

    /**
     * Unified fetch wrapper ensuring session credentials and standard headers
     */
    async apiFetch(endpoint, options = {}) {
        const url = endpoint.startsWith('http') ? endpoint : `${this.API_BASE}${endpoint.startsWith('/') ? '' : '/'}${endpoint}`;
        
        const defaultHeaders = {
            'Accept': 'application/json'
        };

        if (options.body && typeof options.body === 'string') {
            defaultHeaders['Content-Type'] = 'application/json';
        }

        const config = {
            ...options,
            credentials: 'include',
            headers: {
                ...defaultHeaders,
                ...(options.headers || {})
            }
        };

        try {
            const response = await fetch(url, config);
            return response;
        } catch (networkError) {
            console.error('SimUPI Network Request Failed:', url, networkError);
            throw new Error('Unable to connect to SimUPI server. Please ensure the backend server is running on port 8080.');
        }
    },

    /**
     * Formats numeric amounts into Indian Rupee currency format (e.g., ₹1,00,000.00)
     */
    formatINR(amount) {
        if (amount === null || amount === undefined || isNaN(amount)) {
            return '₹0.00';
        }
        const num = parseFloat(amount);
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(num);
    },

    /**
     * Formats ISO date-time into friendly Indian standard format (e.g., 17 Oct 2026, 04:30 PM)
     */
    formatDateTime(dateStr) {
        if (!dateStr) return '';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        });
    },

    /**
     * Displays a floating non-blocking toast notification
     */
    showToast(message, type = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        
        let iconSvg = '';
        if (type === 'success') {
            iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>`;
        } else if (type === 'error') {
            iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>`;
        } else {
            iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>`;
        }

        toast.innerHTML = `${iconSvg} <span>${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-10px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    },

    /**
     * Checks if user has an active session. If not, redirects to login.
     */
    async requireAuth() {
        try {
            const res = await this.apiFetch('/auth/me');
            if (!res.ok) {
                window.location.href = '/pages/login.html';
                return null;
            }
            const body = await res.json();
            if (!body.success || !body.data) {
                window.location.href = '/pages/login.html';
                return null;
            }
            return body.data;
        } catch (err) {
            console.warn('Auth check redirecting to login:', err.message);
            window.location.href = '/pages/login.html';
            return null;
        }
    },

    /**
     * Terminate active user session and return to login
     */
    async logout() {
        try {
            await this.apiFetch('/auth/logout', { method: 'POST' });
        } catch (ignored) {}
        this.showToast('Logged out successfully', 'info');
        setTimeout(() => {
            window.location.href = '/pages/login.html';
        }, 300);
    },

    /**
     * Activates matching navigation icon in both desktop and mobile bars
     */
    initNavigation(activeKey) {
        document.querySelectorAll('.nav-link, .nav-item').forEach(el => {
            el.classList.remove('active');
            if (el.getAttribute('data-nav') === activeKey) {
                el.classList.add('active');
            }
        });
    }
};

window.SimUPI = SimUPI;
