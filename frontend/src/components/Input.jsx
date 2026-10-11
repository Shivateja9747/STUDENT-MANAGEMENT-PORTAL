import React from 'react';
import './Input.css';

const Input = ({
  label,
  error,
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  disabled = false,
  icon: Icon,
  endAction,
  className = '',
  id,
  ...props
}) => {
  const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

  return (
    <div className={`input-group ${className}`}>
      {label && (
        <label htmlFor={inputId} className="input-label">
          {label} {required && <span style={{ color: 'var(--color-danger)' }}>*</span>}
        </label>
      )}
      <div className="input-wrapper">
        {Icon && (
          <span style={{ position: 'absolute', left: '1rem', color: 'var(--ink-muted)', display: 'flex' }}>
            <Icon size={18} />
          </span>
        )}
        <input
          id={inputId}
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          className={`input-field ${error ? 'input-error' : ''}`}
          style={{
            paddingLeft: Icon ? '2.75rem' : '1rem',
            paddingRight: endAction ? '2.75rem' : '1rem'
          }}
          {...props}
        />
        {endAction && (
          <span style={{ position: 'absolute', right: '1rem', display: 'flex' }}>
            {endAction}
          </span>
        )}
      </div>
      {error && <span className="input-error-msg">{error}</span>}
    </div>
  );
};

export default Input;
