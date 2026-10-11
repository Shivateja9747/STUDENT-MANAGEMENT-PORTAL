import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Share2, X, Copy, Trash2, Check, ExternalLink } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './ParentShareModal.css';

const ParentShareModal = ({ isOpen, onClose }) => {
  const { t } = useLanguage();
  const [links, setLinks] = useState([]);
  const [days, setDays] = useState(30);
  const [loading, setLoading] = useState(false);
  const [copiedId, setCopiedId] = useState(null);

  useEffect(() => {
    if (isOpen) {
      fetchLinks();
    }
  }, [isOpen]);

  const fetchLinks = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await axios.get('/api/student/share-link', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setLinks(res.data || []);
    } catch (err) {
      console.error('Fetch share links error:', err);
    }
  };

  const handleCreateLink = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      await axios.post('/api/student/share-link', { durationDays: days }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchLinks();
    } catch (err) {
      console.error('Create share link error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRevoke = async (id) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(`/api/student/share-link/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchLinks();
    } catch (err) {
      console.error('Revoke share link error:', err);
    }
  };

  const copyToClipboard = (tokenStr, id) => {
    const url = `${window.location.origin}/parent/${tokenStr}`;
    navigator.clipboard.writeText(url);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  if (!isOpen) return null;

  return (
    <div className="parent-share-overlay" onClick={onClose}>
      <div className="parent-share-container" onClick={(e) => e.stopPropagation()}>
        <div className="parent-share-header">
          <div className="header-title">
            <Share2 size={20} />
            <span>Parent / Guardian Access Link</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="parent-share-body">
          <p className="description-text">
            Generate an expiring read-only view link for parents showing attendance summary and published marks only.
          </p>

          <div className="create-link-bar">
            <label>Valid Duration:</label>
            <select value={days} onChange={(e) => setDays(parseInt(e.target.value))}>
              <option value={7}>7 Days</option>
              <option value={30}>30 Days</option>
              <option value={90}>90 Days</option>
            </select>
            <button className="primary-btn" onClick={handleCreateLink} disabled={loading}>
              {loading ? t('loading') : 'Generate Link'}
            </button>
          </div>

          <div className="links-list-section">
            <h4>Your Active Share Links</h4>
            {links.length === 0 ? (
              <div className="empty-state">No share links generated yet.</div>
            ) : (
              <div className="links-scroll">
                {links.map((link) => {
                  const isExpired = new Date(link.expiresAt) < new Date();
                  const isRevoked = link.revoked;

                  return (
                    <div key={link.id} className={`link-item-chip ${isRevoked || isExpired ? 'disabled' : ''}`}>
                      <div className="link-info">
                        <span className="token-preview">...{link.token.substring(link.token.length - 8)}</span>
                        <span className="status-text">
                          {isRevoked ? 'Revoked' : isExpired ? 'Expired' : `Expires: ${new Date(link.expiresAt).toLocaleDateString()}`}
                        </span>
                      </div>

                      {!isRevoked && !isExpired && (
                        <div className="actions">
                          <button
                            className="icon-btn copy"
                            onClick={() => copyToClipboard(link.token, link.id)}
                            title="Copy link"
                          >
                            {copiedId === link.id ? <Check size={16} color="#10B981" /> : <Copy size={16} />}
                          </button>
                          <button
                            className="icon-btn open"
                            onClick={() => window.open(`/parent/${link.token}`, '_blank')}
                            title="View Portal"
                          >
                            <ExternalLink size={16} />
                          </button>
                          <button
                            className="icon-btn delete"
                            onClick={() => handleRevoke(link.id)}
                            title="Revoke Link"
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ParentShareModal;
