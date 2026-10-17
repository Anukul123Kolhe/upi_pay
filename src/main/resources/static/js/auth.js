/**
 * SimUPI - Authentication and User Registration Controller
 */

const AuthModule = {
    initRegisterPage() {
        const form = document.getElementById('register-form');
        if (!form) return;

        // Auto preview generated UPI ID
        const nameInput = document.getElementById('reg-name');
        const upiPreview = document.getElementById('upi-preview');
        if (nameInput && upiPreview) {
            nameInput.addEventListener('input', () => {
                const clean = nameInput.value.toLowerCase().replace(/[^a-z0-9]/g, '');
                upiPreview.textContent = (clean || 'yourname') + '@simupi';
            });
        }

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.submitRegister();
        });
    },

    async submitRegister() {
        const payload = {
            name: document.getElementById('reg-name').value.trim(),
            mobile: document.getElementById('reg-mobile').value.trim(),
            email: document.getElementById('reg-email').value.trim(),
            password: document.getElementById('reg-password').value,
            bankName: document.getElementById('reg-bank').value,
            accountType: document.getElementById('reg-type').value,
            openingBalance: parseFloat(document.getElementById('reg-balance').value),
            upiPin: document.getElementById('reg-pin').value.trim()
        };

        const btn = document.getElementById('reg-submit-btn');

        if (!/^[6-9]\d{9}$/.test(payload.mobile)) {
            SimUPI.showToast('Please enter a valid 10-digit Indian mobile number', 'error');
            return;
        }

        if (!/^\d{4,6}$/.test(payload.upiPin)) {
            SimUPI.showToast('UPI PIN must be 4 or 6 digits', 'error');
            return;
        }

        try {
            btn.disabled = true;
            btn.textContent = 'Setting up Account...';

            const res = await SimUPI.apiFetch('/auth/register', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            const data = await res.json();
            if (!res.ok || !data.success) {
                SimUPI.showToast(data.message || 'Registration failed', 'error');
                return;
            }

            SimUPI.showToast('Account created & Bank account linked!', 'success');
            setTimeout(() => {
                SimUPI.goToPage('dashboard.html');
            }, 400);
        } catch (err) {
            SimUPI.showToast(err.message || 'Network error during registration.', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'CREATE ACCOUNT';
        }
    }
};

window.AuthModule = AuthModule;
