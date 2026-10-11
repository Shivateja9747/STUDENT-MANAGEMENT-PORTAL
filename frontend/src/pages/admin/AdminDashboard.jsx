import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { useToast } from '../../context/ToastContext';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import Modal from '../../components/Modal';
import ConfirmDialog from '../../components/ConfirmDialog';
import Input from '../../components/Input';
import Select from '../../components/Select';
import Button from '../../components/Button';
import Card from '../../components/Card';
import Badge from '../../components/Badge';
import { Upload, ArrowUpRight, Key, Bell, Trash2, BarChart2, Users, BookOpen, UserCheck, ShieldAlert, Calendar } from 'lucide-react';
import './AdminDashboard.css';

import Pagination from '../../components/Pagination';
import AuditLogViewer from '../../components/AuditLogViewer';

const VALID_ADMIN_TABS = ['students', 'teachers', 'subjects', 'audit', 'announcements'];

const AdminDashboard = () => {
  const { addToast } = useToast();
  const [searchParams, setSearchParams] = useSearchParams();
  const rawTab = searchParams.get('tab');
  const activeTab = VALID_ADMIN_TABS.includes(rawTab) ? rawTab : 'students';

  const [searchQuery, setSearchQuery] = useState('');
  const [pageNo, setPageNo] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [pagedData, setPagedData] = useState(null);

  // Data states
  const [students, setStudents] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(false);

  // Modal & Dialog states
  const [showFormModal, setShowFormModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const [showCsvModal, setShowCsvModal] = useState(false);
  const [csvContent, setCsvContent] = useState('');
  const [csvTarget, setCsvTarget] = useState('students'); // 'students' | 'teachers'

  const [showPromoteModal, setShowPromoteModal] = useState(false);
  const [promoteDept, setPromoteDept] = useState('Computer Science');
  const [promoteSem, setPromoteSem] = useState(1);

  const [showAnnouncementModal, setShowAnnouncementModal] = useState(false);
  const [announcementForm, setAnnouncementForm] = useState({ title: '', content: '', targetRole: 'ALL' });

  const [deletingId, setDeletingId] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // Forms
  const [studentForm, setStudentForm] = useState({ rollNo: '', name: '', username: '', password: '', department: 'Computer Science', semester: 1 });
  const [teacherForm, setTeacherForm] = useState({ name: '', username: '', password: '', department: 'Computer Science' });
  const [subjectForm, setSubjectForm] = useState({ code: '', name: '', semester: 1, teacherId: '' });

  useEffect(() => {
    fetchCountsAndData();
    if (activeTab === 'announcements') fetchAnnouncements();
  }, [activeTab, pageNo, pageSize, searchQuery]);

  const fetchCountsAndData = async () => {
    setLoading(true);
    try {
      const [studRes, teachRes, subRes] = await Promise.all([
        api.get('/admin/students'),
        api.get('/admin/teachers'),
        api.get('/admin/subjects')
      ]);
      setStudents(studRes.data || []);
      setTeachers(teachRes.data || []);
      setSubjects(subRes.data || []);

      if (activeTab === 'students' || activeTab === 'teachers' || activeTab === 'subjects') {
        const pagedRes = await api.get(`/admin/${activeTab}?page=${pageNo}&size=${pageSize}&search=${encodeURIComponent(searchQuery)}`);
        setPagedData(pagedRes.data);
      }
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to fetch admin records', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchAnnouncements = async () => {
    try {
      const res = await api.get('/announcements');
      setAnnouncements(res.data);
    } catch (err) {
      // Catch silently
    }
  };

  const handleOpenModal = (item = null, tabTarget = activeTab) => {
    setEditingItem(item);
    if (tabTarget === 'students') {
      setStudentForm(item ? {
        rollNo: item.rollNo, name: item.name, username: item.username || '', password: '', department: item.department || 'Computer Science', semester: item.semester || 1
      } : {
        rollNo: '', name: '', username: '', password: '', department: 'Computer Science', semester: 1
      });
    } else if (tabTarget === 'teachers') {
      setTeacherForm(item ? {
        name: item.name, username: item.username || '', password: '', department: item.department || 'Computer Science'
      } : {
        name: '', username: '', password: '', department: 'Computer Science'
      });
    } else if (tabTarget === 'subjects') {
      setSubjectForm(item ? {
        code: item.code, name: item.name, semester: item.semester || 1, teacherId: item.teacherId || ''
      } : {
        code: '', name: '', semester: 1, teacherId: ''
      });
    }
    setShowFormModal(true);
  };

  const handleCloseModal = () => {
    setShowFormModal(false);
    setEditingItem(null);
  };

  const handleStudentSubmit = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      if (editingItem) {
        await api.put(`/admin/students/${editingItem.id}`, studentForm);
        addToast('Student updated successfully!', 'success');
      } else {
        const res = await api.post('/admin/students', studentForm);
        const createdUser = res.data?.username || res.data?.rollNo || studentForm.username;
        const pass = studentForm.password ? studentForm.password : 'student123';
        addToast(`Student created! Login Username: @${createdUser} | Pass: ${pass}`, 'success');
      }
      handleCloseModal();
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'Error saving student record', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleTeacherSubmit = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      if (editingItem) {
        await api.put(`/admin/teachers/${editingItem.id}`, teacherForm);
        addToast('Teacher updated successfully!', 'success');
      } else {
        const res = await api.post('/admin/teachers', teacherForm);
        const createdUser = res.data?.username || teacherForm.username;
        const pass = teacherForm.password ? teacherForm.password : 'teacher123';
        addToast(`Teacher created! Login Username: @${createdUser} | Pass: ${pass}`, 'success');
      }
      handleCloseModal();
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'Error saving teacher record', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleSubjectSubmit = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      const payload = {
        ...subjectForm,
        teacherId: subjectForm.teacherId ? Number(subjectForm.teacherId) : null
      };
      if (editingItem) {
        await api.put(`/admin/subjects/${editingItem.id}`, payload);
        addToast('Subject updated successfully!', 'success');
      } else {
        await api.post('/admin/subjects', payload);
        addToast('Subject created successfully!', 'success');
      }
      handleCloseModal();
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'Error saving subject record', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleBulkCsvImport = async (e) => {
    e.preventDefault();
    if (!csvContent.trim()) {
      addToast('Please paste valid CSV data', 'error');
      return;
    }
    setSubmitLoading(true);
    try {
      const endpoint = csvTarget === 'teachers' ? '/admin/teachers/import' : '/admin/students/import';
      const res = await api.post(endpoint, csvContent, {
        headers: { 'Content-Type': 'text/plain' }
      });
      const count = res.data.successCount !== undefined ? res.data.successCount : '';
      addToast(res.data.message || `Successfully imported ${count} ${csvTarget}!`, 'success');
      setShowCsvModal(false);
      setCsvContent('');
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'CSV Import failed', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDownloadCsvTemplate = async () => {
    try {
      const endpoint = csvTarget === 'teachers' ? '/admin/teachers/import-template' : '/admin/students/import-template';
      const response = await api.get(endpoint, { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${csvTarget}_import_template.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      addToast('Failed to download template', 'error');
    }
  };

  const handlePromoteSemester = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      const res = await api.post(`/admin/students/promote?department=${encodeURIComponent(promoteDept)}&semester=${promoteSem}`);
      addToast(res.data.message || 'Students promoted successfully!', 'success');
      setShowPromoteModal(false);
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'Promotion failed', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleResetPassword = async (userId, username) => {
    if (!window.confirm(`Reset password for user @${username}?`)) return;
    try {
      const res = await api.post(`/admin/users/${userId}/reset-password`);
      addToast(`Password reset successfully for @${username}! New password: ${res.data.newPassword || 'student123'}`, 'success');
    } catch (err) {
      addToast(err.response?.data?.message || 'Password reset failed', 'error');
    }
  };

  const handleCreateAnnouncement = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      const payload = {
        ...announcementForm,
        audience: announcementForm.targetRole || announcementForm.audience || 'ALL'
      };
      await api.post('/admin/announcements', payload);
      addToast('Announcement broadcasted successfully!', 'success');
      setShowAnnouncementModal(false);
      setAnnouncementForm({ title: '', content: '', targetRole: 'ALL' });
      fetchAnnouncements();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to create announcement', 'error');
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDeleteAnnouncement = async (id) => {
    if (!window.confirm('Delete this announcement notice?')) return;
    try {
      await api.delete(`/admin/announcements/${id}`);
      addToast('Announcement deleted', 'info');
      fetchAnnouncements();
    } catch (err) {
      addToast('Failed to delete announcement', 'error');
    }
  };

  const handleConfirmDelete = async () => {
    if (!deletingId) return;
    setDeleteLoading(true);
    try {
      if (activeTab === 'students') await api.delete(`/admin/students/${deletingId}`);
      else if (activeTab === 'teachers') await api.delete(`/admin/teachers/${deletingId}`);
      else if (activeTab === 'subjects') await api.delete(`/admin/subjects/${deletingId}`);

      addToast('Record deleted successfully!', 'success');
      setDeletingId(null);
      fetchCountsAndData();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to delete record', 'error');
    } finally {
      setDeleteLoading(false);
    }
  };

  const filteredStudents = students.filter(s =>
    (s.name || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
    (s.rollNo || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
    (s.department || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredTeachers = teachers.filter(t =>
    (t.name || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
    (t.department || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredSubjects = subjects.filter(s =>
    (s.name || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
    (s.code || '').toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div>
      {/* Header */}
      <div className="admin-header">
        <div className="admin-title-group">
          <h1>
            {activeTab === 'students' ? 'Student Management' :
             activeTab === 'teachers' ? 'Faculty Management' :
             activeTab === 'subjects' ? 'Subject Catalog' :
             activeTab === 'audit' ? 'Audit Trail' : 'Announcements & Broadcasts'}
          </h1>
          <p>Institutional system statistics and administration portal</p>
        </div>
        <div className="admin-user-info">
          <span className="admin-role-badge">ADMIN</span>
          <span className="admin-name-text">Administrator</span>
        </div>
      </div>

      {/* 3 Stat Cards Grid */}
      <div className="admin-stats-grid">
        <div className="admin-stat-card" style={{ cursor: 'pointer' }} onClick={() => setSearchParams({ tab: 'students' })}>
          <div className="admin-stat-label">Total Students</div>
          <div className="admin-stat-value">{students.length}</div>
        </div>

        <div className="admin-stat-card" style={{ cursor: 'pointer' }} onClick={() => setSearchParams({ tab: 'teachers' })}>
          <div className="admin-stat-label">Faculty Teachers</div>
          <div className="admin-stat-value">{teachers.length}</div>
        </div>

        <div className="admin-stat-card" style={{ cursor: 'pointer' }} onClick={() => setSearchParams({ tab: 'subjects' })}>
          <div className="admin-stat-label">Subject Courses</div>
          <div className="admin-stat-value">{subjects.length}</div>
        </div>
      </div>

      {/* TAB: ANNOUNCEMENTS MANAGEMENT */}
      {activeTab === 'announcements' && (
        <Card title="Broadcast Announcements Manager">
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
            <Button onClick={() => setShowAnnouncementModal(true)}>
              + Create New Announcement
            </Button>
          </div>

          {announcements.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {announcements.map(item => (
                <div key={item.id} style={{
                  padding: '1rem',
                  border: '1px solid var(--border-color)',
                  borderRadius: 'var(--radius-md)',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start'
                }}>
                  <div>
                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.4rem' }}>
                      <strong style={{ fontSize: '1rem', color: 'var(--primary-indigo)' }}>{item.title}</strong>
                      <Badge variant="indigo">{item.targetRole || 'ALL'}</Badge>
                    </div>
                    <p style={{ fontSize: '0.9rem', color: 'var(--ink-main)', marginBottom: '0.5rem' }}>{item.content}</p>
                    <span style={{ fontSize: '0.75rem', color: 'var(--ink-muted)' }}>
                      Posted on {new Date(item.createdAt).toLocaleString()}
                    </span>
                  </div>
                  <Button variant="outline" size="sm" onClick={() => handleDeleteAnnouncement(item.id)} style={{ color: 'var(--color-danger)', borderColor: 'var(--color-danger)' }}>
                    <Trash2 size={16} />
                  </Button>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState title="No Active Announcements" description="Create a broadcast notice to inform students and teachers." />
          )}
        </Card>
      )}

      {/* TABS: STUDENTS / TEACHERS / SUBJECTS */}
      {(activeTab === 'students' || activeTab === 'teachers' || activeTab === 'subjects') && (
        <div className="admin-table-card">
          {/* Toolbar */}
          <div className="admin-table-toolbar">
            <div className="admin-search-input">
              <input
                type="text"
                placeholder={activeTab === 'students' ? 'Search by name or roll number' : activeTab === 'teachers' ? 'Search by name or department' : 'Search by code or name'}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            <div style={{ display: 'flex', gap: '0.5rem' }}>
              {(activeTab === 'students' || activeTab === 'teachers') && (
                <Button variant="outline" onClick={() => { setCsvTarget(activeTab); setShowCsvModal(true); }}>
                  <Upload size={16} style={{ marginRight: '0.3rem' }} /> CSV Import
                </Button>
              )}
              <button type="button" className="btn-add-primary" onClick={() => handleOpenModal(null, activeTab)}>
                + Add {activeTab === 'students' ? 'student' : activeTab === 'teachers' ? 'teacher' : 'subject'}
              </button>
            </div>
          </div>

          {/* Content Table Container */}
          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center' }}>
              <Spinner label="Loading admin records..." />
            </div>
          ) : activeTab === 'students' ? (
            filteredStudents.length > 0 ? (
              <div className="admin-table-wrapper">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>ROLL NO</th>
                      <th>NAME</th>
                      <th>USERNAME</th>
                      <th>DEPARTMENT</th>
                      <th>SEMESTER</th>
                      <th style={{ textAlign: 'right' }}>ACTIONS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredStudents.map((s) => (
                      <tr key={s.id}>
                        <td><strong>{s.rollNo}</strong></td>
                        <td>{s.name}</td>
                        <td><Badge variant="indigo">@{s.username || s.rollNo}</Badge></td>
                        <td>{s.department}</td>
                        <td>Sem {s.semester}</td>
                        <td style={{ textAlign: 'right' }}>
                          <button type="button" className="action-btn-edit" title="Reset Password" onClick={() => handleResetPassword(s.userId, s.username || s.rollNo)}>
                            <Key size={14} style={{ marginRight: '2px' }} /> Reset Pass
                          </button>
                          <button type="button" className="action-btn-edit" onClick={() => handleOpenModal(s, 'students')}>Edit</button>
                          <button type="button" className="action-btn-delete" onClick={() => setDeletingId(s.id)}>Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No students found" description="Click '+ Add student' to create a new record." />
            )
          ) : activeTab === 'teachers' ? (
            filteredTeachers.length > 0 ? (
              <div className="admin-table-wrapper">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>NAME</th>
                      <th>USERNAME</th>
                      <th>DEPARTMENT</th>
                      <th style={{ textAlign: 'right' }}>ACTIONS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredTeachers.map((t) => (
                      <tr key={t.id}>
                        <td><strong>#{t.id}</strong></td>
                        <td>{t.name}</td>
                        <td><Badge variant="indigo">@{t.username}</Badge></td>
                        <td>{t.department}</td>
                        <td style={{ textAlign: 'right' }}>
                          <button type="button" className="action-btn-edit" title="Reset Password" onClick={() => handleResetPassword(t.userId, t.username)}>
                            <Key size={14} style={{ marginRight: '2px' }} /> Reset Pass
                          </button>
                          <button type="button" className="action-btn-edit" onClick={() => handleOpenModal(t, 'teachers')}>Edit</button>
                          <button type="button" className="action-btn-delete" onClick={() => setDeletingId(t.id)}>Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No teachers found" description="Click '+ Add teacher' to create a new record." />
            )
          ) : (
            filteredSubjects.length > 0 ? (
              <div className="admin-table-wrapper">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>CODE</th>
                      <th>SUBJECT NAME</th>
                      <th>SEMESTER</th>
                      <th>ASSIGNED TEACHER</th>
                      <th style={{ textAlign: 'right' }}>ACTIONS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredSubjects.map((sub) => (
                      <tr key={sub.id}>
                        <td><strong>{sub.code}</strong></td>
                        <td>{sub.name}</td>
                        <td>Sem {sub.semester}</td>
                        <td>{sub.teacherName || 'Unassigned'}</td>
                        <td style={{ textAlign: 'right' }}>
                          <button type="button" className="action-btn-edit" onClick={() => handleOpenModal(sub, 'subjects')}>Edit</button>
                          <button type="button" className="action-btn-delete" onClick={() => setDeletingId(sub.id)}>Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No subjects found" description="Click '+ Add subject' to create a new record." />
            )
          )}

          {pagedData && (
            <Pagination
              pageNo={pagedData.pageNo ?? pageNo}
              pageSize={pagedData.pageSize ?? pageSize}
              totalElements={pagedData.totalElements ?? 0}
              totalPages={pagedData.totalPages ?? 1}
              onPageChange={(newPage) => setPageNo(newPage)}
              onPageSizeChange={(newSize) => { setPageSize(newSize); setPageNo(0); }}
            />
          )}
        </div>
      )}

      {/* TAB: ACADEMIC CALENDAR */}
      {activeTab === 'calendar' && (
        <AcademicCalendarView isAdmin={true} />
      )}

      {/* TAB: AUDIT LOG TRAIL */}
      {activeTab === 'audit' && (
        <AuditLogViewer />
      )}

      {/* Form Modal */}
      <Modal
        isOpen={showFormModal}
        onClose={handleCloseModal}
        title={`${editingItem ? 'Edit' : 'Add'} ${activeTab === 'students' ? 'Student' : activeTab === 'teachers' ? 'Teacher' : 'Subject'}`}
      >
        {activeTab === 'students' && (
          <form onSubmit={handleStudentSubmit}>
            <Input label="Roll Number" value={studentForm.rollNo} onChange={e => setStudentForm({...studentForm, rollNo: e.target.value})} required />
            <Input label="Full Name" value={studentForm.name} onChange={e => setStudentForm({...studentForm, name: e.target.value})} required />
            <Input label="Username (optional)" value={studentForm.username} onChange={e => setStudentForm({...studentForm, username: e.target.value})} disabled={!!editingItem} />
            <Input label={`Password ${editingItem ? '(leave blank to keep)' : '(optional, default: student123)'}`} type="password" value={studentForm.password} onChange={e => setStudentForm({...studentForm, password: e.target.value})} />
            <Input label="Department" value={studentForm.department} onChange={e => setStudentForm({...studentForm, department: e.target.value})} required />
            <Input label="Semester" type="number" min="1" max="10" value={studentForm.semester} onChange={e => setStudentForm({...studentForm, semester: Number(e.target.value)})} required />
            <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>Save Student</Button>
          </form>
        )}

        {activeTab === 'teachers' && (
          <form onSubmit={handleTeacherSubmit}>
            <Input label="Full Name" value={teacherForm.name} onChange={e => setTeacherForm({...teacherForm, name: e.target.value})} required />
            <Input label="Username (optional)" value={teacherForm.username} onChange={e => setTeacherForm({...teacherForm, username: e.target.value})} disabled={!!editingItem} />
            <Input label={`Password ${editingItem ? '(leave blank to keep)' : '(optional, default: teacher123)'}`} type="password" value={teacherForm.password} onChange={e => setTeacherForm({...teacherForm, password: e.target.value})} />
            <Input label="Department" value={teacherForm.department} onChange={e => setTeacherForm({...teacherForm, department: e.target.value})} required />
            <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>Save Teacher</Button>
          </form>
        )}

        {activeTab === 'subjects' && (
          <form onSubmit={handleSubjectSubmit}>
            <Input label="Subject Code" value={subjectForm.code} onChange={e => setSubjectForm({...subjectForm, code: e.target.value})} required />
            <Input label="Subject Name" value={subjectForm.name} onChange={e => setSubjectForm({...subjectForm, name: e.target.value})} required />
            <Input label="Semester" type="number" min="1" max="10" value={subjectForm.semester} onChange={e => setSubjectForm({...subjectForm, semester: Number(e.target.value)})} required />
            <Select
              label="Assign Teacher"
              value={subjectForm.teacherId}
              onChange={e => setSubjectForm({...subjectForm, teacherId: e.target.value})}
              options={teachers.map(t => ({ value: t.id, label: `${t.name} (${t.department})` }))}
            />
            <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>Save Subject</Button>
          </form>
        )}
      </Modal>

      {/* CSV Bulk Import Modal */}
      <Modal
        isOpen={showCsvModal}
        onClose={() => setShowCsvModal(false)}
        title={`CSV Bulk ${csvTarget === 'teachers' ? 'Faculty/Teacher' : 'Student'} Import`}
      >
        <form onSubmit={handleBulkCsvImport}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
            <p style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', margin: 0 }}>
              {csvTarget === 'teachers'
                ? 'Headers: name,department,[username],[password]'
                : 'Headers: rollNo,name,department,semester'}
            </p>
            <Button type="button" variant="outline" size="sm" onClick={handleDownloadCsvTemplate}>
              Download Template
            </Button>
          </div>
          <textarea
            style={{
              width: '100%',
              height: '160px',
              padding: '0.75rem',
              fontFamily: 'monospace',
              fontSize: '0.85rem',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--radius-md)',
              marginBottom: '1rem'
            }}
            placeholder={csvTarget === 'teachers'
              ? `name,department,username,password\nDr. Ramesh Kumar,Computer Science,prof_ramesh,Teacher@123`
              : `rollNo,name,department,semester\nCS6A01,Aarav Sharma,Computer Science,6`}
            value={csvContent}
            onChange={e => setCsvContent(e.target.value)}
            required
          />
          <Button type="submit" fullWidth loading={submitLoading}>
            Process CSV Import
          </Button>
        </form>
      </Modal>

      {/* Semester Promotion Modal */}
      <Modal
        isOpen={showPromoteModal}
        onClose={() => setShowPromoteModal(false)}
        title="Bulk Semester Promotion"
      >
        <form onSubmit={handlePromoteSemester}>
          <Input label="Department" value={promoteDept} onChange={e => setPromoteDept(e.target.value)} required />
          <Input label="Current Semester to Promote" type="number" min="1" max="8" value={promoteSem} onChange={e => setPromoteSem(Number(e.target.value))} required />
          <p style={{ fontSize: '0.85rem', color: 'var(--ink-muted)', margin: '1rem 0' }}>
            This action will promote all students in <strong>{promoteDept}</strong> currently in <strong>Semester {promoteSem}</strong> to <strong>Semester {promoteSem + 1}</strong>.
          </p>
          <Button type="submit" fullWidth loading={submitLoading}>
            Promote Students Now
          </Button>
        </form>
      </Modal>

      {/* Create Announcement Modal */}
      <Modal
        isOpen={showAnnouncementModal}
        onClose={() => setShowAnnouncementModal(false)}
        title="Broadcast Announcement Notice"
      >
        <form onSubmit={handleCreateAnnouncement}>
          <Input label="Notice Title" value={announcementForm.title} onChange={e => setAnnouncementForm({...announcementForm, title: e.target.value})} required />
          <Input label="Notice Content" value={announcementForm.content} onChange={e => setAnnouncementForm({...announcementForm, content: e.target.value})} required />
          <Select
            label="Target Role"
            value={announcementForm.targetRole}
            onChange={e => setAnnouncementForm({...announcementForm, targetRole: e.target.value})}
            options={[
              { value: 'ALL', label: 'All Users (Teachers & Students)' },
              { value: 'STUDENT', label: 'Students Only' },
              { value: 'TEACHER', label: 'Teachers Only' }
            ]}
          />
          <Button type="submit" fullWidth loading={submitLoading} style={{ marginTop: '1rem' }}>
            Broadcast Notice
          </Button>
        </form>
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deletingId}
        onClose={() => setDeletingId(null)}
        onConfirm={handleConfirmDelete}
        loading={deleteLoading}
        message="Are you sure you want to delete this record? This action cannot be undone."
      />
    </div>
  );
};

export default AdminDashboard;
