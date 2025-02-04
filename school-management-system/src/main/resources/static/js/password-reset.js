document.addEventListener('DOMContentLoaded', function() {
    const resetForm = document.getElementById('resetForm');
    const messageDiv = document.getElementById('message');

    if (resetForm) {
        resetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('email').value;

            fetch('/api/v1/password/reset-request', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email })
            })
            .then(response => {
                if (response.ok) {
                    showMessage('Password reset instructions have been sent to your email.', 'success');
                    resetForm.reset();
                } else {
                    return response.json().then(error => {
                        throw new Error(error.message || 'Failed to request password reset');
                    });
                }
            })
            .catch(error => {
                showMessage(error.message, 'error');
            });
        });
    }

    const updateForm = document.getElementById('updateForm');
    if (updateForm) {
        updateForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const token = new URLSearchParams(window.location.search).get('token');
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (newPassword !== confirmPassword) {
                showMessage('Passwords do not match', 'error');
                return;
            }

            fetch('/api/v1/password/reset', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    token: token,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                })
            })
            .then(response => {
                if (response.ok) {
                    showMessage('Password has been successfully reset. You can now login with your new password.', 'success');
                    updateForm.reset();
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 3000);
                } else {
                    return response.json().then(error => {
                        throw new Error(error.message || 'Failed to reset password');
                    });
                }
            })
            .catch(error => {
                showMessage(error.message, 'error');
            });
        });
    }

    function showMessage(message, type) {
        messageDiv.textContent = message;
        messageDiv.className = `alert alert-${type}`;
        messageDiv.style.display = 'block';

        if (type === 'success') {
            messageDiv.style.backgroundColor = '#d4edda';
            messageDiv.style.color = '#155724';
            messageDiv.style.border = '1px solid #c3e6cb';
        } else {
            messageDiv.style.backgroundColor = '#f8d7da';
            messageDiv.style.color = '#721c24';
            messageDiv.style.border = '1px solid #f5c6cb';
        }

        setTimeout(() => {
            messageDiv.style.display = 'none';
        }, 5000);
    }
});
