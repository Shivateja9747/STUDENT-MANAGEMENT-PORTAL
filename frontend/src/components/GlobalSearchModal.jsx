import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { Search, X, User, BookOpen, Bell, GraduationCap } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import './GlobalSearchModal.css';

const GlobalSearchModal = ({ isOpen, onClose }) => {
  const { t } = useLanguage();
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const inputRef = useRef(null);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (isOpen) onClose();
        else {
          // Open handled by parent or trigger
        }
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 50);
    } else {
      setQuery('');
      setResults([]);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!query.trim() || query.trim().length < 2) {
      setResults([]);
      return;
    }

    const timer = setTimeout(async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get(`/api/search?q=${encodeURIComponent(query.trim())}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setResults(res.data || []);
        setSelectedIndex(0);
      } catch (err) {
        console.error('Search error:', err);
      } finally {
        setLoading(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [query]);

  const handleKeyDownInInput = (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev + 1) % Math.max(1, results.length));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev - 1 + results.length) % Math.max(1, results.length));
    } else if (e.key === 'Escape') {
      onClose();
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'STUDENT': return <GraduationCap size={18} className="result-icon student" />;
      case 'TEACHER': return <User size={18} className="result-icon teacher" />;
      case 'SUBJECT': return <BookOpen size={18} className="result-icon subject" />;
      case 'ANNOUNCEMENT': return <Bell size={18} className="result-icon announcement" />;
      default: return <Search size={18} />;
    }
  };

  if (!isOpen) return null;

  return (
    <div className="global-search-overlay" onClick={onClose}>
      <div className="global-search-container" onClick={(e) => e.stopPropagation()}>
        <div className="global-search-header">
          <Search size={20} className="search-input-icon" />
          <input
            ref={inputRef}
            type="text"
            className="global-search-input"
            placeholder={t('searchPlaceholder')}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleKeyDownInInput}
          />
          <button className="search-close-btn" onClick={onClose} aria-label="Close search">
            <X size={18} />
          </button>
        </div>

        <div className="global-search-body">
          {loading && <div className="search-status">{t('loading')}</div>}

          {!loading && query.trim().length >= 2 && results.length === 0 && (
            <div className="search-status">No matching records found for "{query}".</div>
          )}

          {!loading && results.length > 0 && (
            <div className="search-results-list">
              {results.map((item, idx) => (
                <div
                  key={idx}
                  className={`search-result-item ${idx === selectedIndex ? 'selected' : ''}`}
                  onClick={() => onClose()}
                >
                  {getIcon(item.type)}
                  <div className="result-info">
                    <div className="result-title">{item.title}</div>
                    <div className="result-subtitle">{item.subtitle}</div>
                  </div>
                  <span className={`result-type-badge ${item.type.toLowerCase()}`}>{item.type}</span>
                </div>
              ))}
            </div>
          )}

          {!query && (
            <div className="search-hints">
              <span>Quick shortcut: press <kbd>Ctrl</kbd> + <kbd>K</kbd> anywhere</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GlobalSearchModal;
