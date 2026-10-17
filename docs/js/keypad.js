/**
 * SimUPI - Interactive On-Screen PIN Keypad Controller (PhonePe / GPay inspired)
 * Supports:
 * - On-screen touch/mouse keypad
 * - Physical desktop keyboard input (0-9, Backspace, Enter, Escape)
 * - Centered modal overlay with background click-to-cancel
 */

class UpiKeypad {
    constructor() {
        this.currentPin = '';
        this.maxDigits = 4;
        this.onSubmitCallback = null;
        this.onCancelCallback = null;
        this.isOpen = false;
        this.boundKeyHandler = this.handlePhysicalKey.bind(this);

        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.ensureModalDOM());
        } else {
            this.ensureModalDOM();
        }
    }

    ensureModalDOM() {
        if (document.getElementById('upi-keypad-overlay')) return;
        if (!document.body) return;

        const overlay = document.createElement('div');
        overlay.id = 'upi-keypad-overlay';
        overlay.className = 'keypad-modal-overlay';
        overlay.innerHTML = `
            <div class="keypad-bottom-sheet" id="keypad-card-box">
                <div class="keypad-header">
                    <h3 class="keypad-title" id="keypad-modal-title">ENTER UPI PIN</h3>
                    <p class="keypad-subtitle" id="keypad-modal-subtitle">Submitting payment</p>
                </div>
                
                <div class="pin-dots-container" id="pin-dots-container">
                    <div class="pin-dot"></div>
                    <div class="pin-dot"></div>
                    <div class="pin-dot"></div>
                    <div class="pin-dot"></div>
                </div>

                <div class="keypad-grid">
                    <button type="button" class="key-btn" data-val="1">1</button>
                    <button type="button" class="key-btn" data-val="2">2</button>
                    <button type="button" class="key-btn" data-val="3">3</button>
                    <button type="button" class="key-btn" data-val="4">4</button>
                    <button type="button" class="key-btn" data-val="5">5</button>
                    <button type="button" class="key-btn" data-val="6">6</button>
                    <button type="button" class="key-btn" data-val="7">7</button>
                    <button type="button" class="key-btn" data-val="8">8</button>
                    <button type="button" class="key-btn" data-val="9">9</button>
                    <button type="button" class="key-btn action-btn" id="keypad-cancel-btn" title="Cancel">✕</button>
                    <button type="button" class="key-btn" data-val="0">0</button>
                    <button type="button" class="key-btn action-btn" id="keypad-backspace-btn" title="Delete">⌫</button>
                </div>

                <button type="button" class="btn btn-primary submit-key" id="keypad-submit-btn">
                    CONFIRM PIN
                </button>
            </div>
        `;
        document.body.appendChild(overlay);

        // Click outside the modal box to close
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                this.close();
            }
        });

        // Attach keypad button listeners
        overlay.querySelectorAll('.key-btn[data-val]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.handleKeyPress(btn.getAttribute('data-val'));
            });
        });

        document.getElementById('keypad-backspace-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            this.handleBackspace();
        });

        document.getElementById('keypad-cancel-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            this.close();
        });

        document.getElementById('keypad-submit-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            this.handleSubmit();
        });
    }

    open({ title = 'ENTER UPI PIN', subtitle = 'Please authenticate transaction', maxDigits = 4, onSubmit, onCancel }) {
        this.ensureModalDOM();

        this.currentPin = '';
        this.maxDigits = maxDigits;
        this.onSubmitCallback = onSubmit;
        this.onCancelCallback = onCancel;
        this.isOpen = true;

        const titleEl = document.getElementById('keypad-modal-title');
        const subtitleEl = document.getElementById('keypad-modal-subtitle');
        if (titleEl) titleEl.textContent = title;
        if (subtitleEl) subtitleEl.textContent = subtitle;

        // Render dot placeholders
        const dotsContainer = document.getElementById('pin-dots-container');
        if (dotsContainer) {
            dotsContainer.innerHTML = '';
            for (let i = 0; i < maxDigits; i++) {
                const dot = document.createElement('div');
                dot.className = 'pin-dot';
                dotsContainer.appendChild(dot);
            }
        }

        const overlay = document.getElementById('upi-keypad-overlay');
        if (overlay) {
            overlay.classList.add('show');
        }

        // Add physical keyboard listener
        window.removeEventListener('keydown', this.boundKeyHandler);
        window.addEventListener('keydown', this.boundKeyHandler);

        this.updateDots();
    }

    close() {
        this.isOpen = false;
        window.removeEventListener('keydown', this.boundKeyHandler);

        const overlay = document.getElementById('upi-keypad-overlay');
        if (overlay) {
            overlay.classList.remove('show');
        }

        this.currentPin = '';
        if (this.onCancelCallback) {
            this.onCancelCallback();
        }
    }

    handlePhysicalKey(e) {
        if (!this.isOpen) return;

        if (e.key >= '0' && e.key <= '9') {
            e.preventDefault();
            this.handleKeyPress(e.key);
        } else if (e.key === 'Backspace') {
            e.preventDefault();
            this.handleBackspace();
        } else if (e.key === 'Enter') {
            e.preventDefault();
            this.handleSubmit();
        } else if (e.key === 'Escape') {
            e.preventDefault();
            this.close();
        }
    }

    handleKeyPress(digit) {
        if (this.currentPin.length < this.maxDigits) {
            this.currentPin += digit;
            this.updateDots();

            // Auto-highlight submit button if all digits entered
            if (this.currentPin.length === this.maxDigits) {
                const submitBtn = document.getElementById('keypad-submit-btn');
                if (submitBtn) submitBtn.focus();
            }
        }
    }

    handleBackspace() {
        if (this.currentPin.length > 0) {
            this.currentPin = this.currentPin.slice(0, -1);
            this.updateDots();
        }
    }

    updateDots() {
        const dots = document.querySelectorAll('#pin-dots-container .pin-dot');
        dots.forEach((dot, index) => {
            if (index < this.currentPin.length) {
                dot.classList.add('filled');
            } else {
                dot.classList.remove('filled');
            }
        });
    }

    handleSubmit() {
        if (this.currentPin.length < this.maxDigits) {
            SimUPI.showToast(`Please enter full ${this.maxDigits}-digit UPI PIN`, 'error');
            return;
        }

        const pinToSend = this.currentPin;
        this.isOpen = false;
        window.removeEventListener('keydown', this.boundKeyHandler);

        const overlay = document.getElementById('upi-keypad-overlay');
        if (overlay) {
            overlay.classList.remove('show');
        }

        if (this.onSubmitCallback) {
            this.onSubmitCallback(pinToSend);
        }
    }
}

window.upiKeypad = new UpiKeypad();
