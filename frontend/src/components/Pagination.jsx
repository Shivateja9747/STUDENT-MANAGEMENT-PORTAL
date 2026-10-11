import React from 'react';
import './Pagination.css';

const Pagination = ({
  pageNo = 0,
  pageSize = 10,
  totalElements = 0,
  totalPages = 1,
  onPageChange,
  onPageSizeChange
}) => {
  const startItem = totalElements === 0 ? 0 : pageNo * pageSize + 1;
  const endItem = Math.min((pageNo + 1) * pageSize, totalElements);

  return (
    <div className="pagination-container">
      <div className="pagination-info">
        Showing <strong>{startItem}</strong> to <strong>{endItem}</strong> of <strong>{totalElements}</strong> entries
      </div>

      <div className="pagination-controls">
        <div className="page-size-selector">
          <label>Rows per page:</label>
          <select
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={25}>25</option>
            <option value={50}>50</option>
          </select>
        </div>

        <div className="pagination-buttons">
          <button
            type="button"
            className="pag-btn"
            disabled={pageNo <= 0}
            onClick={() => onPageChange(pageNo - 1)}
          >
            Previous
          </button>
          <span className="page-number-indicator">
            Page {pageNo + 1} of {Math.max(1, totalPages)}
          </span>
          <button
            type="button"
            className="pag-btn"
            disabled={pageNo >= totalPages - 1}
            onClick={() => onPageChange(pageNo + 1)}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
};

export default Pagination;
