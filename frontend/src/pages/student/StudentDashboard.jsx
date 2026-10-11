import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../api/axiosInstance';
import Spinner from '../../components/Spinner';
import EmptyState from '../../components/EmptyState';
import Badge from '../../components/Badge';
import Button from '../../components/Button';
import { Download, FileText, Bell, Award, CheckCircle, AlertTriangle } from 'lucide-react';
import './StudentDashboard.css';

import LeaveManager from '../../components/LeaveManager';
import ClassTimetable from '../../components/ClassTimetable';
import AtRiskPanel from '../../components/AtRiskPanel';

const VALID_STUDENT_TABS = ['overview', 'attendance', 'marks', 'timetable', 'leave'];

const StudentDashboard = () => {
  const [searchParams] = useSearchParams();
  const rawTab = searchParams.get('tab');
  const activeTab = VALID_STUDENT_TABS.includes(rawTab) ? rawTab : 'overview';

  const [summary, setSummary] = useState(null);
  const [detailedAttendance, setDetailedAttendance] = useState([]);
  const [detailedMarks, setDetailedMarks] = useState([]);
  const [announcements, setAnnouncements] = useState([]);

  const [loading, setLoading] = useState(true);
  const [downloadingPdf, setDownloadingPdf] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchStudentData();
    fetchAnnouncements();
  }, [activeTab]);

  const fetchAnnouncements = async () => {
    try {
      const res = await api.get('/announcements');
      setAnnouncements(res.data || []);
    } catch (err) {
      // Catch silently
    }
  };

  const fetchStudentData = async () => {
    setLoading(true);
    setError('');
    try {
      if (activeTab === 'overview' || activeTab === 'bunk') {
        const res = await api.get('/student/performance');
        setSummary(res.data);
      } else if (activeTab === 'attendance') {
        const [sumRes, attRes] = await Promise.all([
          api.get('/student/performance'),
          api.get('/student/attendance')
        ]);
        setSummary(sumRes.data);
        setDetailedAttendance(attRes.data);
      } else if (activeTab === 'marks') {
        const [sumRes, marksRes] = await Promise.all([
          api.get('/student/performance'),
          api.get('/student/marks')
        ]);
        setSummary(sumRes.data);
        setDetailedMarks(marksRes.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load student portal records');
    } finally {
      setLoading(false);
    }
  };

  const downloadPdfReportCard = async () => {
    setDownloadingPdf(true);
    try {
      const response = await api.get('/student/report-card/pdf', {
        responseType: 'blob'
      });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `EduTrack_Report_Card_${summary?.rollNo || 'student'}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert('Failed to download PDF report card.');
    } finally {
      setDownloadingPdf(false);
    }
  };

  const downloadAttendanceCertificate = async () => {
    try {
      const response = await api.get('/student/certificate', { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Attendance_Certificate_${summary?.rollNo || 'student'}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert('Certificate requires >= 75% overall attendance.');
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '4rem', textAlign: 'center' }}>
        <Spinner label="Loading student portal data..." />
      </div>
    );
  }

  if (error) {
    return (
      <div style={{
        backgroundColor: 'var(--bg-danger, #FEE2E2)',
        border: '1px solid var(--color-danger, #B42318)',
        color: 'var(--color-danger, #B42318)',
        padding: '1.25rem',
        borderRadius: 'var(--radius-md, 8px)',
        margin: '2rem auto',
        maxWidth: '600px'
      }}>
        {error}
      </div>
    );
  }

  const lowAttSubject = summary?.subjectAttendances?.find(s => s.percentage < 75);

  return (
    <div>
      {/* Top Header */}
      <div className="student-header">
        <div className="student-title-group">
          <h1>
            {activeTab === 'overview' ? `Hello, ${summary?.studentName || 'Student'}` :
             activeTab === 'attendance' ? 'Attendance Sheet' :
             activeTab === 'marks' ? 'Academic Marks Sheet' :
             activeTab === 'bunk' ? 'Bunk Calculator' :
             activeTab === 'leave' ? 'Leave Portal' : 'Academic Calendar'}
          </h1>
          <p>[{summary?.rollNo || 'Roll No'}] · {summary?.department || 'Engineering'} · Semester {summary?.semester || '1'}</p>
        </div>

        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
          <Button
            variant="outline"
            size="sm"
            onClick={downloadAttendanceCertificate}
            disabled={(summary?.overallAttendancePercentage ?? 0) < 75}
            title={(summary?.overallAttendancePercentage ?? 0) < 75 ? "Requires >= 75% attendance" : "Download Certificate"}
          >
            <Award size={16} style={{ marginRight: '0.4rem' }} /> Certificate
          </Button>
          <Button
            variant="outline"
            size="sm"
            loading={downloadingPdf}
            onClick={downloadPdfReportCard}
          >
            <Download size={16} style={{ marginRight: '0.4rem' }} /> PDF Report Card
          </Button>
          <div className="student-role-badge">
            STUDENT
          </div>
        </div>
      </div>

      {/* Announcements Banner */}
      {announcements.length > 0 && activeTab === 'overview' && (
        <div className="announcements-card" style={{ marginBottom: '1.25rem' }}>
          <div className="announcement-header">
            <Bell size={18} />
            <span>Institute Announcements</span>
          </div>
          <div className="announcement-body">
            {announcements.slice(0, 2).map(anc => (
              <div key={anc.id} className="announcement-row">
                <strong>{anc.title}</strong>: {anc.content}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Low Attendance Warning Banner */}
      {(summary?.hasLowAttendanceWarning || lowAttSubject) && (
        <div className="student-warning-banner">
          <AlertTriangle size={18} style={{ marginRight: '0.5rem' }} />
          <span>Low attendance warning: {lowAttSubject ? `${lowAttSubject.subjectName} (${lowAttSubject.percentage}%)` : 'One or more subjects'} is below 75%. Attend upcoming classes to recover.</span>
        </div>
      )}

      {/* TAB 1: OVERVIEW */}
      {activeTab === 'overview' && (
        <>
          {/* 4 Stat Cards */}
          <div className="student-stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))' }}>
            <div className="student-stat-card">
              <div className="student-stat-label">Overall Attendance</div>
              <div className="student-stat-value">
                {summary?.overallAttendancePercentage?.toFixed(1) ?? '0'}%
              </div>
            </div>

            <div className="student-stat-card">
              <div className="student-stat-label">Semester SGPA</div>
              <div className="student-stat-value" style={{ color: 'var(--primary-indigo)' }}>
                {summary?.sgpa ? summary.sgpa.toFixed(2) : '0.00'}
              </div>
            </div>

            <div className="student-stat-card">
              <div className="student-stat-label">Enrolled Subjects</div>
              <div className="student-stat-value">
                {summary?.subjectAttendances?.length ?? '0'}
              </div>
            </div>

            <div className="student-stat-card">
              <div className="student-stat-label">Average Marks</div>
              <div className="student-stat-value">
                {summary?.overallMarksPercentage?.toFixed(1) ?? '0'}%
              </div>
            </div>
          </div>

          {/* At-Risk Score Panel */}
          <div style={{ marginBottom: '1.5rem' }}>
            <AtRiskPanel role="STUDENT" />
          </div>

          {/* Split Cards Grid */}
          <div className="student-content-grid">
            {/* Attendance by subject Card */}
            <div className="student-card">
              <h2 className="student-card-title">Attendance by Subject</h2>
              
              {summary?.subjectAttendances && summary.subjectAttendances.length > 0 ? (
                summary.subjectAttendances.map((item, idx) => {
                  const pct = item.percentage ?? 0;
                  const colorClass = pct >= 75 ? 'green' : pct >= 65 ? 'amber' : 'red';
                  return (
                    <div key={item.subjectCode || idx} className="subject-progress-item">
                      <div className="subject-progress-header">
                        <span>{item.subjectName || `Subject ${idx + 1}`} ({item.subjectCode})</span>
                        <span className={`subject-progress-value ${colorClass}`}>
                          {pct}%
                        </span>
                      </div>
                      <div className="custom-progress-track">
                        <div
                          className={`custom-progress-fill ${colorClass}`}
                          style={{ width: `${Math.min(100, Math.max(0, pct))}%` }}
                        />
                      </div>
                    </div>
                  );
                })
              ) : (
                <EmptyState title="No attendance data" description="Subject attendance will appear here once logged by your teachers." />
              )}
            </div>

            {/* Marks & Grades Card */}
            <div className="student-card">
              <h2 className="student-card-title">Marks & Grades</h2>
              
              {summary?.marksList && summary.marksList.length > 0 ? (
                <div className="marks-table-wrapper">
                  <table className="marks-mini-table">
                    <thead>
                      <tr>
                        <th>SUBJECT</th>
                        <th>EXAM</th>
                        <th>SCORE</th>
                        <th>GRADE</th>
                      </tr>
                    </thead>
                    <tbody>
                      {summary.marksList.map((m, idx) => {
                        const pct = m.percentage ?? 0;
                        const colorClass = pct >= 75 ? 'green' : pct >= 65 ? 'amber' : 'red';
                        return (
                          <tr key={m.id || idx}>
                            <td><strong>{m.subjectName}</strong></td>
                            <td>{m.examType}</td>
                            <td>{m.isAbsent ? <span style={{ color: 'var(--color-danger)' }}>ABSENT</span> : `${m.marksObtained}/${m.maxMarks}`}</td>
                            <td>
                              <Badge variant={m.grade === 'F' ? 'danger' : 'indigo'}>
                                {m.grade || 'N/A'}
                              </Badge>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              ) : (
                <EmptyState title="No marks entered" description="Exam marks will appear here once submitted by your teachers." />
              )}
            </div>
          </div>
        </>
      )}

      {/* TAB 2: DETAILED ATTENDANCE */}
      {activeTab === 'attendance' && (
        <div className="student-card" style={{ padding: '1.5rem' }}>
          <h2 className="student-card-title">Detailed Course Attendance Records</h2>
          {detailedAttendance.length > 0 ? (
            <div className="marks-table-wrapper">
              <table className="marks-mini-table">
                <thead>
                  <tr>
                    <th>SUBJECT CODE</th>
                    <th>SUBJECT NAME</th>
                    <th>CLASSES ATTENDED</th>
                    <th>EXCUSED LEAVES</th>
                    <th>ATTENDANCE %</th>
                    <th>STATUS</th>
                  </tr>
                </thead>
                <tbody>
                  {detailedAttendance.map((item, idx) => {
                    const pct = item.percentage ?? 0;
                    const colorClass = pct >= 75 ? 'green' : pct >= 65 ? 'amber' : 'red';
                    return (
                      <tr key={item.subjectCode || idx}>
                        <td><strong>[{item.subjectCode}]</strong></td>
                        <td>{item.subjectName}</td>
                        <td>{item.presentClasses} / {item.totalClasses}</td>
                        <td>{item.leaveClasses || 0}</td>
                        <td><strong>{pct.toFixed(1)}%</strong></td>
                        <td>
                          <Badge variant={pct >= 75 ? 'success' : pct >= 65 ? 'warning' : 'danger'}>
                            {pct >= 75 ? 'Good (>= 75%)' : pct >= 65 ? 'Warning (65-74%)' : 'Critical (< 65%)'}
                          </Badge>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState title="No detailed attendance records" description="Attendance logs will appear here once recorded by faculty." />
          )}
        </div>
      )}

      {/* TAB 3: DETAILED MARKS */}
      {activeTab === 'marks' && (
        <div className="student-card" style={{ padding: '1.5rem' }}>
          <h2 className="student-card-title">Detailed Examination Results & Transcripts</h2>
          {detailedMarks.length > 0 ? (
            <div className="marks-table-wrapper">
              <table className="marks-mini-table">
                <thead>
                  <tr>
                    <th>SUBJECT CODE</th>
                    <th>SUBJECT NAME</th>
                    <th>EXAM TYPE</th>
                    <th>MARKS OBTAINED</th>
                    <th>PERCENTAGE</th>
                    <th>GRADE</th>
                    <th>GRADE POINT</th>
                    <th>RESULT</th>
                  </tr>
                </thead>
                <tbody>
                  {detailedMarks.map((m, idx) => {
                    const pct = m.percentage ?? 0;
                    return (
                      <tr key={m.id || idx}>
                        <td><strong>[{m.subjectCode}]</strong></td>
                        <td>{m.subjectName}</td>
                        <td>{m.examType}</td>
                        <td><strong>{m.isAbsent ? 'ABSENT' : `${m.marksObtained} / ${m.maxMarks}`}</strong></td>
                        <td>{pct.toFixed(1)}%</td>
                        <td>
                          <Badge variant={m.grade === 'F' ? 'danger' : 'indigo'}>
                            {m.grade || 'N/A'}
                          </Badge>
                        </td>
                        <td>{m.gradePoint != null ? m.gradePoint.toFixed(1) : '-'}</td>
                        <td>
                          <Badge variant={m.isPassed ? 'success' : 'danger'}>
                            {m.isPassed ? 'PASSED' : 'FAILED'}
                          </Badge>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState title="No marks published" description="Examination scores will appear here once submitted by faculty." />
          )}
        </div>
      )}

      {/* TAB 4: CLASS TIMETABLE */}
      {activeTab === 'timetable' && (
        <ClassTimetable />
      )}

      {/* TAB 5: LEAVE APPLICATIONS */}
      {activeTab === 'leave' && (
        <LeaveManager isTeacherOrHod={false} />
      )}
    </div>
  );
};

export default StudentDashboard;
