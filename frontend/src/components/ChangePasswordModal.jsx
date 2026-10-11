import React, { useState } from 'react';
import Modal from './Modal';
import Input from './Input';
import Button from './Button';
import api from '../api/axiosInstance';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';

const ChangePasswordModal = ({ isOpen, onClose, isMandatory = false }) => {
  const { updateMustChangePassword, logout } = useAuth() || {};
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const toast = useToast();

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!oldPassword.trim()) {
      setError('Current password is required.');
      return;
    }

    if (newPassword.length < 6) {
      setError('New password must be at least 6 characters long.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('New password and confirm password do not match.');
      return;
    }

    setLoading(true);
    try {
      await api.post('/auth/change-password', {
        currentPassword: oldPassword,
        oldPassword: oldPassword,
        newPassword
      });
      toast.success('Password changed successfully!');
      if (updateMustChangePassword) {
        updateMustChangePassword(false);
      }
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      if (onClose) onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={isMandatory ? undefined : onClose}
      title={isMandatory ? 'Action Required: Change Password' : 'Change Password'}
      hideCloseButton={isMandatory}
    >
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {isMandatory && (
          <p style={{ fontSize: '0.875rem', color: 'var(--ink-muted, #5B5876)', marginBottom: '0.5rem', lineHeight: 1.4 }}>
            Your account was created with a temporary password. You must change your password before accessing your portal.
          </p>
        )}

        {error && (
          <div style={{
            padding: '0.65rem 0.85rem',
            background: 'var(--bg-danger, #FEE2E2)',
            color: 'var(--color-danger, #B42318)',
            borderRadius: 'var(--radius-md, 8px)',
            fontSize: '0.85rem'
          }}>
            {error}
          </div>
        )}

        <Input
          label="Current (Temporary) Password"
          type="password"
          placeholder="Enter current password"
          value={oldPassword}
          onChange={(e) => setOldPassword(e.target.value)}
          required
        />

        <Input
          label="New Password"
          type="password"
          placeholder="Enter new password (min 6 characters)"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          required
        />

        <Input
          label="Confirm New Password"
          type="password"
          placeholder="Re-enter new password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '0.5rem' }}>
          {isMandatory ? (
            <Button type="button" variant="secondary" onClick={logout} disabled={loading}>
              Sign Out
            </Button>
          ) : (
            <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
              Cancel
            </Button>
          )}
          <Button type="submit" variant="primary" loading={loading}>
            Update Password
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default ChangePasswordModal;
