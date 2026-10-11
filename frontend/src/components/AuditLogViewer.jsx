import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import { useToast } from '../context/ToastContext';
import Card from './Card';
import Button from './Button';
import Badge from './Badge';
import Spinner from './Spinner';
import EmptyState from './EmptyState';
import Pagination from './Pagination';
import { Download, ShieldAlert } from 'lucide-react';

const AuditLogViewer = () => {
  const { addToast } = useToast();
  const [pagedData, setPagedData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [pageNo, setPageNo] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    fetchAuditLogs();
  }, [pageNo, pageSize, search]);

  const fetchAuditLogs = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/admin/audit-logs?page=${pageNo}&size=${pageSize}&search=${encodeURIComponent(search)}`);
      setPagedData(res.data);
    } catch (err) {
      // Catch silently
    } finally {
      setLoading(false);
    }
  };

  const handleExportCsv = async () => {
    try {
      const response = await api.get('/admin/audit-logs/export', { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'audit_logs.csv');
      document.body.appendChild(link);
      link.click();
      link.remove();
      addToast('Audit log CSV exported successfully!', 'success');
    } catch (err) {
      addToast('Failed to export audit logs CSV', 'error');
    }
  };

  return (
    <Card title="🛡️ System Audit Trail & Compliance Log">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
        <input
          type="text"
          placeholder="Filter by actor, action, or details..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{
            padding: '0.45rem 0.75rem',
            borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--border-color)',
            background: 'var(--bg-app)',
            color: 'var(--ink-main)',
            fontSize: '0.85rem',
            width: '280px'
          }}
        />
        <Button variant="outline" onClick={handleExportCsv}>
          <Download size={16} style={{ marginRight: '0.4rem' }} /> Export Audit CSV
        </Button>
      </div>

      {loading ? (
        <Spinner label="Fetching audit logs..." />
      ) : pagedData?.content?.length > 0 ? (
        <>
          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>TIMESTAMP</th>
                  <th>ACTOR</th>
                  <th>ROLE</th>
                  <th>ACTION</th>
                  <th>DETAILS</th>
                  <th>IP ADDRESS</th>
                </tr>
              </thead>
              <tbody>
                {pagedData.content.map(log => (
                  <tr key={log.id}>
                    <td><small>{new Date(log.timestamp).toLocaleString()}</small></td>
                    <td><strong>{log.actorUsername}</strong></td>
                    <td><Badge variant="indigo">{log.actorRole || 'SYSTEM'}</Badge></td>
                    <td><code>{log.action}</code></td>
                    <td>{log.details}</td>
                    <td><small>{log.ipAddress}</small></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <Pagination
            pageNo={pagedData.pageNo}
            pageSize={pagedData.pageSize}
            totalElements={pagedData.totalElements}
            totalPages={pagedData.totalPages}
            onPageChange={newPage => setPageNo(newPage)}
            onPageSizeChange={newSize => { setPageSize(newSize); setPageNo(0); }}
          />
        </>
      ) : (
        <EmptyState title="No Audit Logs Found" description="System audit events will record here." />
      )}
    </Card>
  );
};

export default AuditLogViewer;
