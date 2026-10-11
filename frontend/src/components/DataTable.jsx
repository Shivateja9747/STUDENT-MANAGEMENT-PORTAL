import React from 'react';
import Spinner from './Spinner';
import EmptyState from './EmptyState';
import './DataTable.css';

const DataTable = ({
  columns = [],
  data = [],
  loading = false,
  emptyMessage = 'No records available',
  className = ''
}) => {
  return (
    <div className={`data-table-card ${className}`}>
      {loading ? (
        <div style={{ padding: '3rem', textAlign: 'center' }}>
          <Spinner label="Loading records..." />
        </div>
      ) : data.length === 0 ? (
        <EmptyState title="No Records" description={emptyMessage} />
      ) : (
        <div className="data-table-scroll">
          <table className="data-table">
            <thead>
              <tr>
                {columns.map((col, idx) => (
                  <th key={idx} style={{ textAlign: col.align || 'left' }}>
                    {col.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.map((row, rowIdx) => (
                <tr key={row.id || rowIdx}>
                  {columns.map((col, colIdx) => (
                    <td key={colIdx} style={{ textAlign: col.align || 'left', whiteSpace: col.nowrap ? 'nowrap' : 'normal' }}>
                      {col.accessor ? col.accessor(row) : row[col.field]}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default DataTable;
