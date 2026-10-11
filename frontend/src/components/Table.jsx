import React from 'react';
import EmptyState from './EmptyState';
import Spinner from './Spinner';

const Table = ({ columns = [], data = [], loading = false, emptyMessage = "No records found" }) => {
  return (
    <div style={{
      width: '100%',
      overflowX: 'auto',
      border: '1px solid #334155',
      borderRadius: '10px',
      backgroundColor: '#1E293B'
    }}>
      {loading ? (
        <div style={{ padding: '3rem', textAlign: 'center' }}>
          <Spinner label="Loading data..." />
        </div>
      ) : data.length === 0 ? (
        <EmptyState title="No Data Available" description={emptyMessage} />
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ backgroundColor: 'rgba(255, 255, 255, 0.03)', borderBottom: '1px solid #334155' }}>
              {columns.map((col, idx) => (
                <th
                  key={idx}
                  style={{
                    padding: '0.85rem 1rem',
                    fontSize: '0.75rem',
                    fontWeight: 700,
                    color: '#94A3B8',
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em',
                    whiteSpace: 'nowrap',
                    position: 'sticky',
                    top: 0
                  }}
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((row, rowIdx) => (
              <tr
                key={row.id || rowIdx}
                style={{
                  borderBottom: '1px solid #334155',
                  backgroundColor: rowIdx % 2 === 1 ? 'rgba(255, 255, 255, 0.015)' : 'transparent',
                  transition: 'background-color 0.15s ease'
                }}
              >
                {columns.map((col, colIdx) => (
                  <td
                    key={colIdx}
                    style={{
                      padding: '0.85rem 1rem',
                      fontSize: '0.9rem',
                      color: '#F8FAFC',
                      whiteSpace: col.nowrap ? 'nowrap' : 'normal'
                    }}
                  >
                    {col.accessor ? col.accessor(row) : row[col.field]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Table;
