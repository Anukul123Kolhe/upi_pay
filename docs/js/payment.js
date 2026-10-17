/**
 * SimUPI - Payment Controller (Send Money, Recipient Verification, Keypad Trigger)
 */

const PaymentModule = {
    verifiedRecipient: null,
    lookupDebounceTimer: null,

    initSendPage() {
        const queryInput = document.getElementById('receiver-query');
        const badge = document.getElementById('receiver-verify-badge');

        // Pre-fill if receiver passed in URL query param
        const urlParams = new URLSearchParams(window.location.search);
        const prefillReceiver = urlParams.get('to');
        if (prefillReceiver && queryInput) {
            queryInput.value = prefillReceiver;
            this.verifyRecipient(prefillReceiver);
        }

        if (queryInput) {
            queryInput.addEventListener('input', () => {
                clearTimeout(this.lookupDebounceTimer);
                const val = queryInput.value.trim();
                if (val.length >= 3) {
                    this.lookupDebounceTimer = setTimeout(() => this.verifyRecipient(val), 350);
                } else {
                    this.verifiedRecipient = null;
                    if (badge) badge.style.display = 'none';
                    const verifiedCard = document.getElementById('verified-recipient-card');
                    if (verifiedCard) verifiedCard.style.display = 'none';
                }
            });
        }

        // Quick amount chip selectors
        document.querySelectorAll('.amount-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                const amount = chip.getAttribute('data-amt');
                const amtInput = document.getElementById('payment-amount');
                if (amtInput) amtInput.value = amount;
            });
        });

        // Pay Button
        const payBtn = document.getElementById('initiate-pay-btn');
        if (payBtn) {
            payBtn.addEventListener('click', () => this.handleInitiatePayment());
        }
    },

    async verifyRecipient(query) {
        const badge = document.getElementById('receiver-verify-badge');
        const nameDisplay = document.getElementById('receiver-verified-name');
        const verifiedCard = document.getElementById('verified-recipient-card');

        try {
            if (badge) {
                badge.style.display = 'inline-flex';
                badge.className = 'verify-status-badge checking';
            }
            if (nameDisplay) nameDisplay.textContent = 'Verifying receiver...';

            const res = await SimUPI.apiFetch(`/payments/search?query=${encodeURIComponent(query)}`);
            const data = await res.json();

            if (res.ok && data.success && data.data) {
                this.verifiedRecipient = data.data;
                if (badge) {
                    badge.className = 'verify-status-badge verified';
                }
                if (nameDisplay) {
                    nameDisplay.textContent = `Verified: ${data.data.name} (${data.data.upiId})`;
                }
                if (verifiedCard) {
                    verifiedCard.style.display = 'flex';
                    const avatar = document.getElementById('verified-avatar');
                    if (avatar) avatar.textContent = data.data.name.charAt(0).toUpperCase();
                    const vName = document.getElementById('verified-name');
                    if (vName) vName.textContent = data.data.name;
                    const vUpi = document.getElementById('verified-upi');
                    if (vUpi) vUpi.textContent = data.data.upiId;
                }
            } else {
                this.verifiedRecipient = null;
                if (badge) badge.className = 'verify-status-badge not-found';
                if (nameDisplay) nameDisplay.textContent = 'User not found in UPI network';
                if (verifiedCard) verifiedCard.style.display = 'none';
            }
        } catch (err) {
            if (badge) badge.className = 'verify-status-badge not-found';
            if (nameDisplay) nameDisplay.textContent = 'Verification error';
            if (verifiedCard) verifiedCard.style.display = 'none';
        }
    },

    handleInitiatePayment() {
        const query = document.getElementById('receiver-query').value.trim();
        const amountStr = document.getElementById('payment-amount').value.trim();
        const note = document.getElementById('payment-note').value.trim();

        if (!query) {
            SimUPI.showToast('Please enter recipient UPI ID or Mobile', 'error');
            return;
        }

        const amount = parseFloat(amountStr);
        if (isNaN(amount) || amount <= 0) {
            SimUPI.showToast('Please enter a valid amount greater than ₹0.00', 'error');
            return;
        }

        if (amount > 100000) {
            SimUPI.showToast('Maximum transfer limit per transaction is ₹1,00,000.00', 'error');
            return;
        }

        const recipientLabel = this.verifiedRecipient ? this.verifiedRecipient.name : query;

        // Trigger On-Screen PIN Keypad Modal
        window.upiKeypad.open({
            title: 'ENTER UPI PIN',
            subtitle: `Sending ${SimUPI.formatINR(amount)} to ${recipientLabel}`,
            maxDigits: 4,
            onSubmit: (enteredPin) => {
                this.executeTransfer(query, amount, note, enteredPin);
            }
        });
    },

    async executeTransfer(receiverQuery, amount, note, upiPin) {
        const formScreen = document.getElementById('send-form-screen');
        const successScreen = document.getElementById('payment-success-screen');
        const failureScreen = document.getElementById('payment-failure-screen');

        try {
            const res = await SimUPI.apiFetch('/payments/send', {
                method: 'POST',
                body: JSON.stringify({ receiverQuery, amount, note, upiPin })
            });

            const result = await res.json();

            if (res.ok && result.success) {
                // Render Success Animation Screen
                if (formScreen) formScreen.style.display = 'none';
                if (failureScreen) failureScreen.classList.remove('active');
                if (successScreen) {
                    successScreen.classList.add('active');
                    document.getElementById('success-amount-text').textContent = SimUPI.formatINR(result.data.amount);
                    document.getElementById('success-receiver-name').textContent = result.data.receiverName;
                    document.getElementById('success-utr-text').textContent = result.data.utr;
                    document.getElementById('success-receipt-btn').href = `receipt.html?utr=${result.data.utr}`;
                }
            } else {
                // Render Failure Screen
                if (formScreen) formScreen.style.display = 'none';
                if (successScreen) successScreen.classList.remove('active');
                if (failureScreen) {
                    failureScreen.classList.add('active');
                    document.getElementById('failure-reason-text').textContent = result.message || 'Payment could not be processed';
                }
            }
        } catch (err) {
            if (formScreen) formScreen.style.display = 'none';
            if (failureScreen) {
                failureScreen.classList.add('active');
                document.getElementById('failure-reason-text').textContent = err.message || 'Network communication failure. Please check backend connection.';
            }
        }
    }
};

window.PaymentModule = PaymentModule;
