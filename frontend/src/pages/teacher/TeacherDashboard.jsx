import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { useToast } from '../../context/ToastContext';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import Select from '../../components/Select';
import Input from '../../components/Input';
import Button from '../../components/Button';
import Badge from '../../components/Badge';
import Card from '../../components/Card';
import { Download, AlertTriangle, Calendar, FileText, Award } from 'lucide-react';
import './TeacherDashboard.css';

import LeaveManager from '../../components/LeaveManager';
import AttendanceCorrectionManager from '../../components/AttendanceCorrectionManager';
import TodaysClassesCard from '../../components/TodaysClassesCard';
import AtRiskPanel from '../../components/AtRiskPanel';

const VALID_TEACHER_TABS = ['attendance', 'today', 'leaves', 'disputes', 'atrisk', 'history', 'marks'];

const TeacherDashboard = () => {
  const { addToast } = useToast();
  const [searchParams, setSearchParams] = useSearchParams();
  const rawTab = searchParams.get('tab');
  const activeTab = VALID_TEACHER_TABS.includes(rawTab) ? rawTab : 'attendance';

  const [subjects, setSubjects] = useState([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState('');

  // Attendance Sheet States
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [period, setPeriod] = useState(1);
  const [students, setStudents] = useState([]);
  const [attendanceMap, setAttendanceMap] = useState({});
  const [remarksMap, setRemarksMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // History Matrix States
  const [historyMonth, setHistoryMonth] = useState(new Date().getMonth() + 1);
  const [historyYear, setHistoryYear] = useState(new Date().getFullYear());
  const [historyMatrix, setHistoryMatrix] = useState([]);

  // Marks Entry States
  const [selectedExamType, setSelectedExamType] = useState('MID1');
  const [maxMarks, setMaxMarks] = useState(100);
  const [marksMap, setMarksMap] = useState({});
  const [absentMarksMap, setAbsentMarksMap] = useState({});
  const [marksStats, setMarksStats] = useState(null);

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const res = await api.get('/teacher/subjects');
      setSubjects(res.data);
      if (res.data.length > 0 && !selectedSubjectId) {
        setSelectedSubjectId(res.data[0].id.toString());
      }
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to load assigned subjects', 'error');
    }
  };

  useEffect(() => {
    if (!selectedSubjectId) return;
    if (activeTab === 'attendance') {
      loadAttendanceRoster();
    } else if (activeTab === 'history') {
      loadAttendanceHistory();
    } else if (activeTab === 'marks') {
      loadMarksRoster();
      loadMarksStats();
    }
  }, [selectedSubjectId, selectedDate, period, activeTab, selectedExamType, historyMonth, historyYear]);

  // Date restriction validation (7 days window)
  const isDateEditable = () => {
    const target = new Date(selectedDate);
    const today = new Date();
    today.setHours(0,0,0,0);
    const diffTime = today - target;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays >= 0 && diffDays <= 7;
  };

  const loadAttendanceRoster = async () => {
    if (!selectedSubjectId || !selectedDate) return;
    setLoading(true);
    try {
      const studentRes = await api.get(`/teacher/subjects/${selectedSubjectId}/students`);
      const studentList = studentRes.data;
      setStudents(studentList);

      const attendanceRes = await api.get(`/teacher/attendance?subjectId=${selectedSubjectId}&date=${selectedDate}&period=${period}`);
      const existingRecords = attendanceRes.data;

      const initialMap = {};
      const initialRemarks = {};
      studentList.forEach(s => {
        const found = existingRecords.find(r => r.studentId === s.id);
        initialMap[s.id] = found ? found.status : 'PRESENT';
        initialRemarks[s.id] = found ? (found.remark || '') : '';
      });
      setAttendanceMap(initialMap);
      setRemarksMap(initialRemarks);
    } catch (err) {
      addToast(err.response?.data?.message || 'Error loading student roster', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadAttendanceHistory = async () => {
    if (!selectedSubjectId) return;
    setLoading(true);
    try {
      const res = await api.get(`/teacher/attendance/history?subjectId=${selectedSubjectId}&month=${historyMonth}&year=${historyYear}`);
      setHistoryMatrix(res.data || []);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to load history matrix', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadDefaulters = async () => {
    if (!selectedSubjectId) return;
    setLoading(true);
    try {
      const res = await api.get(`/teacher/defaulters?threshold=${threshold}`);
      const filtered = res.data.filter(d => d.subjectId.toString() === selectedSubjectId || selectedSubjectId === 'all');
      setDefaultersList(filtered.length > 0 ? filtered : res.data);
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to load defaulters', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadMarksRoster = async () => {
    if (!selectedSubjectId || !selectedExamType) return;
    setLoading(true);
    try {
      const studentRes = await api.get(`/teacher/subjects/${selectedSubjectId}/students`);
      const studentList = studentRes.data;
      setStudents(studentList);

      const marksRes = await api.get(`/teacher/marks?subjectId=${selectedSubjectId}&examType=${selectedExamType}`);
      const existingMarks = marksRes.data;

      const initialMap = {};
      const initialAbsentMap = {};
      let currentMax = 100;
      studentList.forEach(s => {
        const found = existingMarks.find(r => r.studentId === s.id);
        initialMap[s.id] = found ? found.marksObtained : 0;
        initialAbsentMap[s.id] = found ? (found.isAbsent || false) : false;
        if (found && found.maxMarks) {
          currentMax = found.maxMarks;
        }
      });
      setMaxMarks(currentMax);
      setMarksMap(initialMap);
      setAbsentMarksMap(initialAbsentMap);
    } catch (err) {
      addToast(err.response?.data?.message || 'Error loading marks roster', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadMarksStats = async () => {
    if (!selectedSubjectId || !selectedExamType) return;
    try {
      const res = await api.get(`/teacher/marks/stats?subjectId=${selectedSubjectId}&examType=${selectedExamType}`);
      setMarksStats(res.data);
    } catch (err) {
      setMarksStats(null);
    }
  };

  const handleToggleStatus = (studentId, status) => {
    setAttendanceMap(prev => ({ ...prev, [studentId]: status }));
  };

  const handleRemarkChange = (studentId, remarkText) => {
    setRemarksMap(prev => ({ ...prev, [studentId]: remarkText }));
  };

  const handleMarkAll = (status) => {
    const updated = {};
    students.forEach(s => { updated[s.id] = status; });
    setAttendanceMap(updated);
  };

  const handleSaveAttendance = async () => {
    if (!selectedSubjectId || !selectedDate) return;
    if (!isDateEditable()) {
      addToast('Cannot edit attendance older than 7 days or in future.', 'error');
      return;
    }

    setSubmitting(true);

    const records = Object.keys(attendanceMap).map(studentId => ({
      studentId: Number(studentId),
      status: attendanceMap[studentId],
      remark: remarksMap[studentId] || ''
    }));

    try {
      await api.post('/teacher/attendance', {
        subjectId: Number(selectedSubjectId),
        date: selectedDate,
        period: Number(period),
        records
      });
      addToast('Attendance sheet saved successfully!', 'success');
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save attendance', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleSaveMarks = async () => {
    if (!selectedSubjectId || !selectedExamType) return;
    setSubmitting(true);

    const records = Object.keys(marksMap).map(studentId => ({
      studentId: Number(studentId),
      marksObtained: absentMarksMap[studentId] ? 0 : Number(marksMap[studentId]),
      maxMarks: Number(maxMarks),
      isAbsent: !!absentMarksMap[studentId]
    }));

    try {
      await api.post('/teacher/marks', {
        subjectId: Number(selectedSubjectId),
        examType: selectedExamType,
        records
      });
      addToast('Marks updated successfully!', 'success');
      loadMarksStats();
    } catch (err) {
      addToast(err.response?.data?.message || 'Failed to save marks', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const exportDefaultersCSV = () => {
    if (defaultersList.length === 0) {
      addToast('No defaulters to export', 'info');
      return;
    }
    const headers = ['Roll No', 'Student Name', 'Department', 'Semester', 'Total Classes', 'Attended', 'Attendance %'];
    const rows = defaultersList.map(d => [
      d.rollNo,
      `"${d.studentName}"`,
      d.department,
      d.semester,
      d.totalClasses,
      d.attendedClasses,
      `${d.attendancePercentage}%`
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `Defaulters_List_${selectedSubjectId}_${threshold}percent.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Live Attendance Counts
  const presentCount = Object.values(attendanceMap).filter(st => st === 'PRESENT').length;
  const absentCount = Object.values(attendanceMap).filter(st => st === 'ABSENT').length;
  const lateCount = Object.values(attendanceMap).filter(st => st === 'LATE').length;
  const leaveCount = Object.values(attendanceMap).filter(st => st === 'LEAVE').length;

  const currentSubject = subjects.find(s => s.id.toString() === selectedSubjectId);

  return (
    <div>
      {/* Header */}
      <div className="teacher-header">
        <div className="teacher-title-group">
          <h1>
            {activeTab === 'subjects' ? 'My Assigned Subjects' :
             activeTab === 'attendance' ? 'Mark Attendance' :
             activeTab === 'history' ? 'Monthly Attendance Matrix' :
             activeTab === 'defaulters' ? 'Attendance Defaulters' : 'Marks & Evaluation'}
          </h1>
          <p>
            {currentSubject ? `${currentSubject.name} (${currentSubject.code}) · Semester ${currentSubject.semester}` : 'Faculty course management portal'}
          </p>
        </div>
      </div>

      {/* Course Selector Bar */}
      {activeTab !== 'subjects' && subjects.length > 0 && (
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.25rem', flexWrap: 'wrap', alignItems: 'center' }}>
          <div style={{ flex: 1, minWidth: '240px' }}>
            <Select
              label="Active Subject Course"
              value={selectedSubjectId}
              onChange={e => setSelectedSubjectId(e.target.value)}
              options={subjects.map(s => ({ value: s.id.toString(), label: `${s.name} (${s.code}) - Sem ${s.semester}` }))}
            />
          </div>
        </div>
      )}

      {subjects.length === 0 && activeTab !== 'subjects' && (
        <Card style={{ textAlign: 'center', padding: '2.5rem', marginBottom: '1.5rem' }}>
          <AlertTriangle size={32} style={{ color: 'var(--color-warning)', marginBottom: '0.75rem' }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: 'var(--ink-main)', marginBottom: '0.5rem' }}>
            No Subject Courses Assigned Yet
          </h3>
          <p style={{ color: 'var(--ink-muted)', fontSize: '0.9rem' }}>
            Your teacher account is logged in successfully. Please ask an Administrator to assign course subjects to your profile in the Admin Portal.
          </p>
        </Card>
      )}

      {/* TAB 1: MY SUBJECTS */}
      {activeTab === 'subjects' && (
        <div>
          {subjects.length === 0 ? (
            <Card style={{ textAlign: 'center', padding: '3rem' }}>
              <p style={{ color: 'var(--ink-muted)' }}>No course subjects assigned to your teacher profile yet.</p>
            </Card>
          ) : (
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: '1.25rem'
            }}>
              {subjects.map(sub => {
                const isSelected = sub.id.toString() === selectedSubjectId;
                return (
                  <div
                    key={sub.id}
                    style={{
                      backgroundColor: 'var(--bg-card)',
                      border: `1.5px solid ${isSelected ? 'var(--primary-indigo)' : 'rgba(213, 212, 227, 0.6)'}`,
                      borderRadius: 'var(--radius-lg)',
                      padding: '1.5rem',
                      boxShadow: 'var(--shadow-card)',
                      transition: 'all 0.2s ease'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                      <Badge variant="indigo">{sub.code}</Badge>
                      <span style={{ fontSize: '0.8rem', color: 'var(--ink-muted)', fontWeight: 600 }}>Semester {sub.semester}</span>
                    </div>

                    <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--ink-main)', marginBottom: '1rem' }}>
                      {sub.name}
                    </h3>

                    <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', flexWrap: 'wrap' }}>
                      <Button
                        size="sm"
                        variant="primary"
                        onClick={() => {
                          setSelectedSubjectId(sub.id.toString());
                          setSearchParams({ tab: 'attendance' });
                        }}
                        style={{ flex: 1 }}
                      >
                        Mark Attendance
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => {
                          setSelectedSubjectId(sub.id.toString());
                          setSearchParams({ tab: 'marks' });
                        }}
                        style={{ flex: 1 }}
                      >
                        Enter Marks
                      </Button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* TAB 2: ATTENDANCE MARKING */}
      {activeTab === 'attendance' && (
        <>
          {/* Controls Bar */}
          <div className="teacher-controls-bar">
            <div className="teacher-controls-left" style={{ flexWrap: 'wrap', gap: '1rem' }}>
              <div className="teacher-date-picker">
                <label>Date</label>
                <input
                  type="date"
                  value={selectedDate}
                  max={new Date().toISOString().split('T')[0]}
                  onChange={e => setSelectedDate(e.target.value)}
                />
              </div>

              <div style={{ width: '130px' }}>
                <Select
                  label="Period"
                  value={period.toString()}
                  onChange={e => setPeriod(Number(e.target.value))}
                  options={[
                    { value: '1', label: 'Period 1' },
                    { value: '2', label: 'Period 2' },
                    { value: '3', label: 'Period 3' },
                    { value: '4', label: 'Period 4' },
                    { value: '5', label: 'Period 5' },
                    { value: '6', label: 'Period 6' },
                    { value: '7', label: 'Period 7' },
                    { value: '8', label: 'Period 8' }
                  ]}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.4rem', marginTop: '1.4rem' }}>
                <button type="button" className="btn-mark-all" onClick={() => handleMarkAll('PRESENT')}>
                  All Present
                </button>
                <button type="button" className="btn-mark-all" onClick={() => handleMarkAll('ABSENT')} style={{ borderColor: 'var(--color-danger)' }}>
                  All Absent
                </button>
              </div>
            </div>

            <div className="teacher-controls-right" style={{ gap: '0.5rem', flexWrap: 'wrap' }}>
              <span className="status-count-pill present">Present (P): {presentCount}</span>
              <span className="status-count-pill absent">Absent (A): {absentCount}</span>
            </div>
          </div>

          {!isDateEditable() && (
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.75rem 1rem',
              background: '#FEF3C7',
              color: '#92400E',
              borderRadius: 'var(--radius-md)',
              marginBottom: '1rem',
              fontSize: '0.875rem'
            }}>
              <AlertTriangle size={18} />
              <span>Notice: Attendance for dates older than 7 days is read-only according to policy.</span>
            </div>
          )}

          {/* Attendance Table Card */}
          <div className="attendance-card">
            {loading ? (
              <div style={{ padding: '3rem', textAlign: 'center' }}>
                <Spinner label="Loading attendance roster..." />
              </div>
            ) : students.length > 0 ? (
              <div className="attendance-table-wrapper">
                <table className="attendance-table">
                  <thead>
                    <tr>
                      <th>ROLL NO</th>
                      <th>STUDENT NAME</th>
                      <th style={{ textAlign: 'center' }}>ATTENDANCE STATUS</th>
                      <th>REMARK (OPTIONAL)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((student) => {
                      const status = attendanceMap[student.id] || 'PRESENT';
                      return (
                        <tr key={student.id}>
                          <td><strong>{student.rollNo}</strong></td>
                          <td>{student.name}</td>
                          <td style={{ textAlign: 'center' }}>
                            <div className="segmented-toggle-2">
                              <button
                                type="button"
                                className={`seg-btn present ${status === 'PRESENT' ? 'active' : ''}`}
                                onClick={() => handleToggleStatus(student.id, 'PRESENT')}
                                disabled={!isDateEditable()}
                              >
                                Present (P)
                              </button>
                              <button
                                type="button"
                                className={`seg-btn absent ${status === 'ABSENT' ? 'active' : ''}`}
                                onClick={() => handleToggleStatus(student.id, 'ABSENT')}
                                disabled={!isDateEditable()}
                              >
                                Absent (A)
                              </button>
                            </div>
                          </td>
                          <td>
                            <input
                              type="text"
                              className="table-input-sm"
                              placeholder="Add remark..."
                              value={remarksMap[student.id] || ''}
                              onChange={e => handleRemarkChange(student.id, e.target.value)}
                              disabled={!isDateEditable()}
                            />
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No students found" description="No enrolled students found for this subject." />
            )}
          </div>

          {/* Action Footer */}
          <div className="attendance-action-footer">
            <button type="button" className="btn-cancel" onClick={loadAttendanceRoster}>
              Cancel
            </button>
            <button type="button" className="btn-save-attendance" disabled={submitting || !isDateEditable()} onClick={handleSaveAttendance}>
              {submitting ? 'Saving...' : 'Save Attendance'}
            </button>
          </div>
        </>
      )}

      {/* TAB 3: MONTHLY HISTORY MATRIX */}
      {activeTab === 'history' && (
        <div>
          <div className="teacher-controls-bar">
            <div className="teacher-controls-left" style={{ gap: '1rem' }}>
              <div style={{ width: '140px' }}>
                <Select
                  label="Month"
                  value={historyMonth.toString()}
                  onChange={e => setHistoryMonth(Number(e.target.value))}
                  options={[
                    { value: '1', label: 'January' },
                    { value: '2', label: 'February' },
                    { value: '3', label: 'March' },
                    { value: '4', label: 'April' },
                    { value: '5', label: 'May' },
                    { value: '6', label: 'June' },
                    { value: '7', label: 'July' },
                    { value: '8', label: 'August' },
                    { value: '9', label: 'September' },
                    { value: '10', label: 'October' },
                    { value: '11', label: 'November' },
                    { value: '12', label: 'December' }
                  ]}
                />
              </div>
              <div style={{ width: '110px' }}>
                <Input
                  label="Year"
                  type="number"
                  value={historyYear}
                  onChange={e => setHistoryYear(Number(e.target.value))}
                />
              </div>
            </div>
          </div>

          <div className="attendance-card">
            {loading ? (
              <div style={{ padding: '3rem', textAlign: 'center' }}>
                <Spinner label="Loading attendance history matrix..." />
              </div>
            ) : historyMatrix.length > 0 ? (
              <div className="attendance-table-wrapper">
                <table className="attendance-table matrix-table">
                  <thead>
                    <tr>
                      <th>Roll No</th>
                      <th>Student Name</th>
                      {Array.from({ length: 31 }, (_, i) => (
                        <th key={i + 1} style={{ textAlign: 'center', minWidth: '28px' }}>{i + 1}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {historyMatrix.map(row => (
                      <tr key={row.studentId}>
                        <td><strong>{row.rollNo}</strong></td>
                        <td>{row.studentName}</td>
                        {Array.from({ length: 31 }, (_, i) => {
                          const dayNum = i + 1;
                          const st = row.dailyStatus ? row.dailyStatus[dayNum] : null;
                          return (
                            <td key={dayNum} style={{ textAlign: 'center', padding: '0.2rem' }}>
                              {st === 'PRESENT' && <span className="matrix-badge present">P</span>}
                              {st === 'ABSENT' && <span className="matrix-badge absent">A</span>}
                              {st === 'LATE' && <span className="matrix-badge late">L</span>}
                              {st === 'LEAVE' && <span className="matrix-badge leave">LV</span>}
                              {!st && <span className="matrix-badge empty">-</span>}
                            </td>
                          );
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No History Records" description="No attendance records logged for this month." />
            )}
          </div>
        </div>
      )}

      {/* TAB 4: DEFAULTERS LIST */}
      {activeTab === 'defaulters' && (
        <div>
          <div className="teacher-controls-bar">
            <div className="teacher-controls-left" style={{ gap: '1rem', alignItems: 'center' }}>
              <div style={{ width: '160px' }}>
                <Select
                  label="Min Threshold"
                  value={threshold.toString()}
                  onChange={e => setThreshold(Number(e.target.value))}
                  options={[
                    { value: '75', label: 'Below 75%' },
                    { value: '65', label: 'Below 65%' },
                    { value: '80', label: 'Below 80%' },
                    { value: '50', label: 'Below 50%' }
                  ]}
                />
              </div>
            </div>

            <Button variant="outline" onClick={exportDefaultersCSV}>
              <Download size={16} style={{ marginRight: '0.4rem' }} /> Export to CSV
            </Button>
          </div>

          <div className="attendance-card">
            {loading ? (
              <div style={{ padding: '3rem', textAlign: 'center' }}>
                <Spinner label="Loading defaulters list..." />
              </div>
            ) : defaultersList.length > 0 ? (
              <div className="attendance-table-wrapper">
                <table className="attendance-table">
                  <thead>
                    <tr>
                      <th>ROLL NO</th>
                      <th>STUDENT NAME</th>
                      <th>DEPARTMENT</th>
                      <th>SEMESTER</th>
                      <th>ATTENDED / TOTAL</th>
                      <th>ATTENDANCE %</th>
                      <th>STATUS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {defaultersList.map(item => (
                      <tr key={item.studentId}>
                        <td><strong>{item.rollNo}</strong></td>
                        <td>{item.studentName}</td>
                        <td>{item.department}</td>
                        <td>Sem {item.semester}</td>
                        <td>{item.attendedClasses} / {item.totalClasses}</td>
                        <td>
                          <strong style={{ color: item.attendancePercentage < 75 ? 'var(--color-danger)' : 'var(--color-warning)' }}>
                            {item.attendancePercentage.toFixed(1)}%
                          </strong>
                        </td>
                        <td>
                          <Badge variant={item.attendancePercentage < 75 ? 'danger' : 'warning'}>
                            {item.attendancePercentage < 75 ? 'CRITICAL DEFAULTER' : 'WARNING'}
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState title="No Defaulters Found" description={`Great news! All students have attendance equal to or above ${threshold}%.`} />
            )}
          </div>
        </div>
      )}

      {/* TAB 5: MARKS ENTRY */}
      {activeTab === 'marks' && (
        <div className="attendance-card" style={{ padding: '1.5rem' }}>
          {/* Marks Stats Banner */}
          {marksStats && (
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
              gap: '1rem',
              marginBottom: '1.5rem',
              background: 'var(--bg-neutral, #F4F5FB)',
              padding: '1rem',
              borderRadius: 'var(--radius-md)'
            }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--ink-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Class Average</span>
                <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary-indigo)' }}>
                  {marksStats.classAverage != null ? `${marksStats.classAverage.toFixed(1)} / ${marksStats.maxMarks}` : 'N/A'}
                </div>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--ink-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Highest Score</span>
                <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--color-success)' }}>
                  {marksStats.highestScore != null ? marksStats.highestScore : 'N/A'}
                </div>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--ink-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Pass Rate</span>
                <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--ink-main)' }}>
                  {marksStats.passPercentage != null ? `${marksStats.passPercentage.toFixed(1)}% (${marksStats.passedCount}/${marksStats.totalStudents})` : 'N/A'}
                </div>
              </div>
            </div>
          )}

          <div style={{ display: 'flex', gap: '1.5rem', marginBottom: '1.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
            <div style={{ flex: 1, minWidth: '180px' }}>
              <Select
                label="Exam Type"
                value={selectedExamType}
                onChange={e => setSelectedExamType(e.target.value)}
                options={[
                  { value: 'MID1', label: 'Mid Semester 1' },
                  { value: 'MID2', label: 'Mid Semester 2' },
                  { value: 'FINAL', label: 'End Semester Final' },
                  { value: 'ASSIGNMENT', label: 'Assignment / Quiz' }
                ]}
              />
            </div>
            <div style={{ width: '140px' }}>
              <Input
                label="Max Marks"
                type="number"
                min="1"
                value={maxMarks}
                onChange={e => setMaxMarks(Number(e.target.value))}
              />
            </div>
          </div>

          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center' }}>
              <Spinner label="Loading marks roster..." />
            </div>
          ) : students.length > 0 ? (
            <div className="attendance-table-wrapper">
              <table className="attendance-table">
                <thead>
                  <tr>
                    <th>ROLL NO</th>
                    <th>STUDENT NAME</th>
                    <th>ABSENT EXAM</th>
                    <th>MARKS OBTAINED (MAX {maxMarks})</th>
                  </tr>
                </thead>
                <tbody>
                  {students.map((student) => {
                    const isAbsent = !!absentMarksMap[student.id];
                    return (
                      <tr key={student.id}>
                        <td><strong>{student.rollNo}</strong></td>
                        <td>{student.name}</td>
                        <td>
                          <label style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', cursor: 'pointer' }}>
                            <input
                              type="checkbox"
                              checked={isAbsent}
                              onChange={e => setAbsentMarksMap({ ...absentMarksMap, [student.id]: e.target.checked })}
                            />
                            <span style={{ fontSize: '0.85rem' }}>Absent</span>
                          </label>
                        </td>
                        <td>
                          <div style={{ width: '140px' }}>
                            <Input
                              type="number"
                              min="0"
                              max={maxMarks}
                              disabled={isAbsent}
                              value={isAbsent ? 0 : (marksMap[student.id] ?? 0)}
                              onChange={e => setMarksMap({ ...marksMap, [student.id]: Number(e.target.value) })}
                            />
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState title="No students found" description="No students enrolled for this course semester." />
          )}

          <div className="attendance-action-footer">
            <button type="button" className="btn-cancel" onClick={loadMarksRoster}>
              Cancel
            </button>
            <button type="button" className="btn-save-attendance" disabled={submitting} onClick={handleSaveMarks}>
              {submitting ? 'Saving...' : 'Save Marks'}
            </button>
          </div>
        </div>
      )}

      {/* TAB 5: TODAY'S SCHEDULE */}
      {activeTab === 'today' && (
        <TodaysClassesCard />
      )}

      {/* TAB 6: LEAVE REQUESTS INBOX */}
      {activeTab === 'leaves' && (
        <LeaveManager isTeacherOrHod={true} />
      )}

      {/* TAB 7: DISPUTES INBOX */}
      {activeTab === 'disputes' && (
        <AttendanceCorrectionManager isTeacherOrHod={true} />
      )}

      {/* TAB 8: AT-RISK PANEL */}
      {activeTab === 'atrisk' && (
        <AtRiskPanel role="TEACHER" />
      )}
    </div>
  );
};

export default TeacherDashboard;
