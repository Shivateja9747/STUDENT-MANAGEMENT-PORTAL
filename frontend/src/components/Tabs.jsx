import React from 'react';

const Tabs = ({ tabs = [], activeTab, onChange }) => {
  return (
    <div style={{
      display: 'flex',
      gap: '0.5rem',
      borderBottom: '1px solid #334155',
      marginBottom: '1.5rem',
      overflowX: 'auto'
    }}>
      {tabs.map((tab) => {
        const isActive = activeTab === tab.id;
        const Icon = tab.icon;
        return (
          <button
            key={tab.id}
            onClick={() => onChange(tab.id)}
            style={{
              background: 'none',
              border: 'none',
              borderBottom: `2px solid ${isActive ? '#4F46E5' : 'transparent'}`,
              color: isActive ? '#818CF8' : '#94A3B8',
              padding: '0.75rem 1.25rem',
              fontWeight: isActive ? 600 : 500,
              fontSize: '0.925rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              whiteSpace: 'nowrap',
              transition: 'all 0.2s ease'
            }}
          >
            {Icon && <Icon size={18} />}
            {tab.label}
          </button>
        );
      })}
    </div>
  );
};

export default Tabs;
