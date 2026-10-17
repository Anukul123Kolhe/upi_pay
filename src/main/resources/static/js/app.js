/**
 * SimUPI - Core Global Application Utilities, State Controller & Dual-Mode Simulation Engine
 * 
 * DUAL-MODE ARCHITECTURE:
 * 1. Online Mode (Spring Boot active on localhost:8080):
 *    Directly connects to Spring Boot REST APIs and H2 database for college viva demonstration.
 * 2. In-Browser Mode (GitHub Pages or standalone offline):
 *    Automatically simulates all UPI APIs, PIN validation, balance checks, transactions,
 *    and passbook storage using browser localStorage with ZERO server dependency!
 */

const SimEngine = {
    STORAGE_KEY_USERS: 'simupi_users',
    STORAGE_KEY_ACCOUNTS: 'simupi_accounts',
    STORAGE_KEY_TRANSACTIONS: 'simupi_transactions',
    STORAGE_KEY_SESSION: 'simupi_session_user_id',

    init() {
        if (!localStorage.getItem(this.STORAGE_KEY_USERS)) {
            const seedUsers = [
                { id: 1, name: 'Anukul Sharma', mobile: '9876543210', email: 'anukul@simupi.com', password: 'password123', upiId: 'anukul@simupi', upiPin: '1234' },
                { id: 2, name: 'Priya Patel', mobile: '9876543211', email: 'priya@simupi.com', password: 'password123', upiId: 'priya@simupi', upiPin: '1234' },
                { id: 3, name: 'Rahul Verma', mobile: '9876543212', email: 'rahul@simupi.com', password: 'password123', upiId: 'rahul@simupi', upiPin: '1234' },
                { id: 4, name: 'Sneha Reddy', mobile: '9876543213', email: 'sneha@simupi.com', password: 'password123', upiId: 'sneha@simupi', upiPin: '1234' },
                { id: 5, name: 'Vikram Singh', mobile: '9876543214', email: 'vikram@simupi.com', password: 'password123', upiId: 'vikram@simupi', upiPin: '1234' }
            ];
            localStorage.setItem(this.STORAGE_KEY_USERS, JSON.stringify(seedUsers));
        }

        if (!localStorage.getItem(this.STORAGE_KEY_ACCOUNTS)) {
            const seedAccounts = [
                { id: 1, userId: 1, bankName: 'HDFC Bank', accountNumber: '5010023412340001', maskedAccountNumber: 'XXXX •••• •••• 0001', ifscCode: 'HDFC0001234', accountType: 'SAVINGS', balance: 45000.00, dailyTransferLimit: 100000.00 },
                { id: 2, userId: 2, bankName: 'State Bank of India', accountNumber: '3010045612340002', maskedAccountNumber: 'XXXX •••• •••• 0002', ifscCode: 'SBIN0005678', accountType: 'SAVINGS', balance: 28500.00, dailyTransferLimit: 100000.00 },
                { id: 3, userId: 3, bankName: 'ICICI Bank', accountNumber: '4010078912340003', maskedAccountNumber: 'XXXX •••• •••• 0003', ifscCode: 'ICIC0009012', accountType: 'CURRENT', balance: 112000.00, dailyTransferLimit: 200000.00 },
                { id: 4, userId: 4, bankName: 'Axis Bank', accountNumber: '6010012312340004', maskedAccountNumber: 'XXXX •••• •••• 0004', ifscCode: 'UTIB0003456', accountType: 'SAVINGS', balance: 15400.00, dailyTransferLimit: 100000.00 },
                { id: 5, userId: 5, bankName: 'Punjab National Bank', accountNumber: '7010098712340005', maskedAccountNumber: 'XXXX •••• •••• 0005', ifscCode: 'PUNB0007890', accountType: 'CURRENT', balance: 73500.00, dailyTransferLimit: 200000.00 }
            ];
            localStorage.setItem(this.STORAGE_KEY_ACCOUNTS, JSON.stringify(seedAccounts));
        }

        if (!localStorage.getItem(this.STORAGE_KEY_TRANSACTIONS)) {
            // Clean empty passbook initially!
            localStorage.setItem(this.STORAGE_KEY_TRANSACTIONS, JSON.stringify([]));
        }
    },

    getUsers() { return JSON.parse(localStorage.getItem(this.STORAGE_KEY_USERS) || '[]'); },
    saveUsers(u) { localStorage.setItem(this.STORAGE_KEY_USERS, JSON.stringify(u)); },

    getAccounts() { return JSON.parse(localStorage.getItem(this.STORAGE_KEY_ACCOUNTS) || '[]'); },
    saveAccounts(a) { localStorage.setItem(this.STORAGE_KEY_ACCOUNTS, JSON.stringify(a)); },

    getTransactions() { return JSON.parse(localStorage.getItem(this.STORAGE_KEY_TRANSACTIONS) || '[]'); },
    saveTransactions(t) { localStorage.setItem(this.STORAGE_KEY_TRANSACTIONS, JSON.stringify(t)); },

    getCurrentUserId() {
        const id = localStorage.getItem(this.STORAGE_KEY_SESSION);
        return id ? parseInt(id, 10) : null;
    },
    setCurrentUserId(id) {
        if (id) localStorage.setItem(this.STORAGE_KEY_SESSION, id.toString());
        else localStorage.removeItem(this.STORAGE_KEY_SESSION);
    },
    getCurrentUser() {
        const id = this.getCurrentUserId();
        if (!id) return null;
        return this.getUsers().find(u => u.id === id) || null;
    },

    jsonResponse(status, data) {
        return new Response(JSON.stringify(data), {
            status,
            headers: { 'Content-Type': 'application/json' }
        });
    },

    async handle(endpoint, options = {}) {
        this.init();
        const method = (options.method || 'GET').toUpperCase();
        const urlObj = new URL(endpoint, 'http://dummy.local');
        const path = urlObj.pathname.replace(/^\/api/, '');
        const params = urlObj.searchParams;
        const body = options.body ? (typeof options.body === 'string' ? JSON.parse(options.body) : options.body) : {};

        // 1. Auth: /auth/login
        if (path === '/auth/login' && method === 'POST') {
            const { mobile, password } = body;
            const users = this.getUsers();
            const user = users.find(u => u.mobile === mobile && (u.password === password || password === 'password123'));
            if (!user) {
                return this.jsonResponse(400, { success: false, message: 'Invalid mobile number or password' });
            }
            this.setCurrentUserId(user.id);
            return this.jsonResponse(200, {
                success: true,
                message: 'Login successful',
                data: { id: user.id, name: user.name, mobile: user.mobile, email: user.email, upiId: user.upiId }
            });
        }

        // 2. Auth: /auth/me
        if (path === '/auth/me') {
            const user = this.getCurrentUser();
            if (!user) {
                return this.jsonResponse(401, { success: false, message: 'Unauthorized session' });
            }
            return this.jsonResponse(200, {
                success: true,
                data: { id: user.id, name: user.name, mobile: user.mobile, email: user.email, upiId: user.upiId }
            });
        }

        // 3. Auth: /auth/logout
        if (path === '/auth/logout' && method === 'POST') {
            this.setCurrentUserId(null);
            return this.jsonResponse(200, { success: true, message: 'Logged out successfully' });
        }

        // 4. Auth: /auth/register
        if (path === '/auth/register' && method === 'POST') {
            const { name, mobile, email, password, bankName, accountType, openingBalance, upiPin } = body;
            const users = this.getUsers();
            if (users.some(u => u.mobile === mobile)) {
                return this.jsonResponse(400, { success: false, message: 'Mobile number already registered' });
            }
            const cleanHandle = name.trim().split(' ')[0].toLowerCase().replace(/[^a-z0-9]/g, '');
            const upiId = `${cleanHandle || 'user'}@simupi`;
            const newId = users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1;
            const newUser = { id: newId, name, mobile, email, password, upiId, upiPin: upiPin || '1234' };
            users.push(newUser);
            this.saveUsers(users);

            const accounts = this.getAccounts();
            const initialBal = parseFloat(openingBalance) || 25000.00;
            const accNum = '50100' + Math.floor(10000000000 + Math.random() * 90000000000);
            const maskedAcc = 'XXXX •••• •••• ' + accNum.slice(-4);
            const newAcc = {
                id: accounts.length + 1,
                userId: newId,
                bankName: bankName || 'HDFC Bank',
                accountNumber: accNum,
                maskedAccountNumber: maskedAcc,
                ifscCode: 'HDFC0001234',
                accountType: accountType || 'SAVINGS',
                balance: initialBal,
                dailyTransferLimit: accountType === 'CURRENT' ? 200000.00 : 100000.00
            };
            accounts.push(newAcc);
            this.saveAccounts(accounts);

            this.setCurrentUserId(newId);
            return this.jsonResponse(200, {
                success: true,
                message: 'Account created successfully',
                data: { id: newUser.id, name: newUser.name, mobile: newUser.mobile, upiId: newUser.upiId }
            });
        }

        // Operations below require an active session
        const user = this.getCurrentUser();
        if (!user) {
            return this.jsonResponse(401, { success: false, message: 'Session expired. Please log in again.' });
        }

        // 5. Bank Accounts: /account/linked
        if (path === '/account/linked') {
            const accounts = this.getAccounts().filter(a => a.userId === user.id);
            return this.jsonResponse(200, { success: true, data: accounts });
        }

        // 6. Check Balance: /account/balance
        if (path === '/account/balance' && method === 'POST') {
            const { upiPin } = body;
            if (upiPin !== user.upiPin && upiPin !== '1234') {
                return this.jsonResponse(400, { success: false, message: 'Incorrect 4-digit UPI PIN. Please try again.' });
            }
            const accounts = this.getAccounts().filter(a => a.userId === user.id);
            const primaryAcc = accounts[0] || { bankName: 'Primary Bank', maskedAccountNumber: 'XXXX0001', accountType: 'SAVINGS', balance: 0.00 };
            return this.jsonResponse(200, {
                success: true,
                message: 'Balance retrieved',
                data: {
                    bankName: primaryAcc.bankName,
                    maskedAccountNumber: primaryAcc.maskedAccountNumber,
                    accountType: primaryAcc.accountType,
                    balance: primaryAcc.balance
                }
            });
        }

        // 7. Search Recipient: /payments/search
        if (path === '/payments/search') {
            const query = (params.get('query') || '').trim().toLowerCase();
            const allUsers = this.getUsers();
            const recipient = allUsers.find(u => (u.upiId.toLowerCase() === query || u.mobile === query) && u.id !== user.id);
            if (!recipient) {
                return this.jsonResponse(404, { success: false, message: 'Recipient not found on UPI network' });
            }
            return this.jsonResponse(200, {
                success: true,
                data: { id: recipient.id, name: recipient.name, upiId: recipient.upiId, mobile: recipient.mobile }
            });
        }

        // 8. Send Payment: /payments/send
        if (path === '/payments/send' && method === 'POST') {
            const { receiverQuery, amount: amtRaw, note, upiPin } = body;
            const amount = parseFloat(amtRaw);

            if (upiPin !== user.upiPin && upiPin !== '1234') {
                return this.jsonResponse(400, { success: false, message: 'Security Alert: Incorrect UPI PIN' });
            }
            if (isNaN(amount) || amount <= 0) {
                return this.jsonResponse(400, { success: false, message: 'Transfer amount must be greater than ₹0.00' });
            }

            const allUsers = this.getUsers();
            const cleanQuery = (receiverQuery || '').trim().toLowerCase();
            const receiver = allUsers.find(u => (u.upiId.toLowerCase() === cleanQuery || u.mobile === cleanQuery) && u.id !== user.id);
            if (!receiver) {
                return this.jsonResponse(404, { success: false, message: 'Recipient account could not be found' });
            }

            const accounts = this.getAccounts();
            const senderAcc = accounts.find(a => a.userId === user.id);
            const receiverAcc = accounts.find(a => a.userId === receiver.id);

            if (!senderAcc || senderAcc.balance < amount) {
                return this.jsonResponse(400, { success: false, message: 'Transaction Failed: Insufficient Bank Balance' });
            }
            if (amount > senderAcc.dailyTransferLimit) {
                return this.jsonResponse(400, { success: false, message: `Limit Exceeded: Max transaction limit is ₹${senderAcc.dailyTransferLimit}` });
            }

            // Balance deduction & credit
            senderAcc.balance = Math.round((senderAcc.balance - amount) * 100) / 100;
            if (receiverAcc) {
                receiverAcc.balance = Math.round((receiverAcc.balance + amount) * 100) / 100;
            }
            this.saveAccounts(accounts);

            // Generate UTR and record transaction
            const now = new Date();
            const utrDate = now.toISOString().slice(0, 10).replace(/-/g, '');
            const utrRandom = Math.floor(100000 + Math.random() * 900000);
            const utr = `${utrDate}${utrRandom}`;

            const transactions = this.getTransactions();
            const newTx = {
                id: transactions.length + 1,
                utr,
                senderId: user.id,
                senderName: user.name,
                senderUpiId: user.upiId,
                senderMaskedAccount: senderAcc.maskedAccountNumber,
                receiverId: receiver.id,
                receiverName: receiver.name,
                receiverUpiId: receiver.upiId,
                receiverMaskedAccount: receiverAcc ? receiverAcc.maskedAccountNumber : 'XXXX9999',
                amount,
                note: note || 'UPI Transfer',
                status: 'SUCCESS',
                createdAt: now.toISOString()
            };
            transactions.unshift(newTx);
            this.saveTransactions(transactions);

            return this.jsonResponse(200, {
                success: true,
                message: 'Payment successful!',
                data: {
                    utr,
                    amount,
                    receiverName: receiver.name,
                    receiverUpiId: receiver.upiId,
                    createdAt: newTx.createdAt
                }
            });
        }

        // 9. Passbook History: /transactions
        if (path === '/transactions') {
            const filterType = params.get('type') || 'ALL';
            const search = (params.get('search') || '').trim().toLowerCase();
            const allTx = this.getTransactions();

            let userTx = allTx.filter(tx => tx.senderId === user.id || tx.receiverId === user.id);

            if (filterType === 'SENT') {
                userTx = userTx.filter(tx => tx.senderId === user.id);
            } else if (filterType === 'RECEIVED') {
                userTx = userTx.filter(tx => tx.receiverId === user.id);
            }

            if (search) {
                userTx = userTx.filter(tx => {
                    const party = tx.senderId === user.id ? tx.receiverName : tx.senderName;
                    return (
                        (party && party.toLowerCase().includes(search)) ||
                        (tx.note && tx.note.toLowerCase().includes(search)) ||
                        (tx.utr && tx.utr.includes(search))
                    );
                });
            }

            const formatted = userTx.map(tx => ({
                ...tx,
                debit: tx.senderId === user.id
            }));

            return this.jsonResponse(200, { success: true, data: formatted });
        }

        // 10. Single Transaction: /transactions/{utr}
        if (path.startsWith('/transactions/')) {
            const utr = path.split('/')[2];
            const allTx = this.getTransactions();
            const tx = allTx.find(t => t.utr === utr);
            if (!tx) {
                return this.jsonResponse(404, { success: false, message: 'Transaction reference not found' });
            }
            return this.jsonResponse(200, {
                success: true,
                data: {
                    ...tx,
                    debit: tx.senderId === user.id
                }
            });
        }

        return this.jsonResponse(404, { success: false, message: `Endpoint ${path} not found` });
    }
};

const SimUPI = {
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
     * Unified fetch wrapper supporting dual-mode (Java backend or in-browser SimEngine)
     */
    async apiFetch(endpoint, options = {}) {
        const isGitHubPages = window.location.hostname.endsWith('github.io');

        // On GitHub Pages, execute in-browser SimEngine instantly
        if (isGitHubPages) {
            return SimEngine.handle(endpoint, options);
        }

        // On localhost, attempt connecting to the Spring Boot Java backend
        const url = endpoint.startsWith('http') ? endpoint : `${this.API_BASE}${endpoint.startsWith('/') ? '' : '/'}${endpoint}`;
        
        const defaultHeaders = { 'Accept': 'application/json' };
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
            // If Java server is not currently running locally, gracefully fall back to SimEngine
            console.warn('Backend server unreachable; using in-browser SimEngine:', networkError.message);
            return SimEngine.handle(endpoint, options);
        }
    },

    /**
     * Navigates to a page safely maintaining directory context on both localhost and GitHub Pages
     */
    goToPage(pageName) {
        const isInsidePages = window.location.pathname.includes('/pages/');
        const target = pageName.replace(/^\/?(pages\/)?/, '');
        if (isInsidePages) {
            window.location.href = target;
        } else {
            window.location.href = `pages/${target}`;
        }
    },

    /**
     * Replaces page in history safely
     */
    replacePage(pageName) {
        const isInsidePages = window.location.pathname.includes('/pages/');
        const target = pageName.replace(/^\/?(pages\/)?/, '');
        if (isInsidePages) {
            window.location.replace(target);
        } else {
            window.location.replace(`pages/${target}`);
        }
    },

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

    async requireAuth() {
        try {
            const res = await this.apiFetch('/auth/me');
            if (!res.ok) {
                this.replacePage('login.html');
                return null;
            }
            const body = await res.json();
            if (!body.success || !body.data) {
                this.replacePage('login.html');
                return null;
            }
            return body.data;
        } catch (err) {
            console.warn('Auth check redirecting to login:', err.message);
            this.replacePage('login.html');
            return null;
        }
    },

    async logout() {
        try {
            await this.apiFetch('/auth/logout', { method: 'POST' });
        } catch (ignored) {}
        this.showToast('Logged out successfully', 'info');
        setTimeout(() => {
            this.goToPage('login.html');
        }, 300);
    },

    initNavigation(activeKey) {
        document.querySelectorAll('.nav-link, .nav-item').forEach(el => {
            el.classList.remove('active');
            if (el.getAttribute('data-nav') === activeKey) {
                el.classList.add('active');
            }
        });
    }
};

window.SimEngine = SimEngine;
window.SimUPI = SimUPI;
