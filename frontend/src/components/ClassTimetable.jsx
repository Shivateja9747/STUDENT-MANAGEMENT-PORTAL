import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import Spinner from './Spinner';
import Badge from './Badge';
import {
  MapPin,
  User,
  Calendar as CalendarIcon,
  Clock,
  BookOpen,
  Award,
  Layers,
  Printer,
  Sparkles,
  Info
} from 'lucide-react';
import './ClassTimetable.css';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
const PERIOD_INFO = [
  { period: 1, label: 'P1', time: '09:00 - 09:50 AM' },
  { period: 2, label: 'P2', time: '09:55 - 10:45 AM' },
  { period: 3, label: 'P3', time: '10:55 - 11:45 AM' },
  { period: 4, label: 'P4', time: '11:50 - 12:40 PM' },
  // Lunch 12:40 - 01:30 PM
  { period: 5, label: 'P5', time: '01:30 - 02:20 PM' },
  { period: 6, label: 'P6', time: '02:25 - 03:15 PM' },
  { period: 7, label: 'P7', time: '03:25 - 04:15 PM' },
  { period: 8, label: 'P8', time: '04:15 - 05:00 PM' }
];

export const BTECH_SAMPLE_TIMETABLE = [
  // MONDAY
  {
    dayOfWeek: 'MONDAY',
    period: 1,
    subjectCode: 'CS-201',
    subjectName: 'Data Structures & Algorithms',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. K. Ramanathan'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 2,
    subjectCode: 'CS-202',
    subjectName: 'Database Management Systems',
    type: 'Theory',
    room: 'LH-102 (Block C)',
    teacherName: 'Prof. Shalini Verma'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 3,
    subjectCode: 'MA-201',
    subjectName: 'Discrete Mathematics & Graph Theory',
    type: 'Tutorial',
    room: 'LH-204 (Block A)',
    teacherName: 'Dr. S. K. Mahapatra'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 4,
    subjectCode: 'CS-203',
    subjectName: 'Computer Organization & Architecture',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. Amitav Ghosh'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 5,
    subjectCode: 'CS-204',
    subjectName: 'Operating Systems & System Calls',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Rajesh Nair'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 6,
    subjectCode: 'HS-201',
    subjectName: 'Technical Communication & Soft Skills',
    type: 'Seminar',
    room: 'Seminar Hall 2',
    teacherName: 'Dr. Meera Nambiar'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 7,
    subjectCode: 'CS-201L',
    subjectName: 'Data Structures Lab (C++/Java)',
    type: 'Lab',
    room: 'CC-Lab 1 (CS Block)',
    teacherName: 'Dr. K. Ramanathan & Asst'
  },
  {
    dayOfWeek: 'MONDAY',
    period: 8,
    subjectCode: 'CS-201L',
    subjectName: 'Data Structures Lab (Practical Evaluation)',
    type: 'Lab',
    room: 'CC-Lab 1 (CS Block)',
    teacherName: 'Dr. K. Ramanathan & Asst'
  },

  // TUESDAY
  {
    dayOfWeek: 'TUESDAY',
    period: 1,
    subjectCode: 'CS-204',
    subjectName: 'Operating Systems & System Calls',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Rajesh Nair'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 2,
    subjectCode: 'CS-201',
    subjectName: 'Data Structures & Algorithms',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. K. Ramanathan'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 3,
    subjectCode: 'EC-202',
    subjectName: 'Digital Electronics & Logic Design',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Naveen Chawla'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 4,
    subjectCode: 'CS-202',
    subjectName: 'Database Management Systems',
    type: 'Theory',
    room: 'LH-102 (Block C)',
    teacherName: 'Prof. Shalini Verma'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 5,
    subjectCode: 'MA-201',
    subjectName: 'Discrete Mathematics & Graph Theory',
    type: 'Theory',
    room: 'LH-204 (Block A)',
    teacherName: 'Dr. S. K. Mahapatra'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 6,
    subjectCode: 'LIB-101',
    subjectName: 'Library & Guided Technical Reading',
    type: 'Tutorial',
    room: 'Central Library 2F',
    teacherName: 'Faculty Mentor'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 7,
    subjectCode: 'CS-202L',
    subjectName: 'RDBMS & SQL Query Optimization Lab',
    type: 'Lab',
    room: 'DB-Lab 2 (CS Block)',
    teacherName: 'Prof. Shalini Verma'
  },
  {
    dayOfWeek: 'TUESDAY',
    period: 8,
    subjectCode: 'CS-202L',
    subjectName: 'RDBMS & SQL Query Optimization Lab (Cont)',
    type: 'Lab',
    room: 'DB-Lab 2 (CS Block)',
    teacherName: 'Prof. Shalini Verma'
  },

  // WEDNESDAY
  {
    dayOfWeek: 'WEDNESDAY',
    period: 1,
    subjectCode: 'CS-205',
    subjectName: 'Object Oriented Programming (Java)',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Prof. Ananya Roy'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 2,
    subjectCode: 'CS-203',
    subjectName: 'Computer Organization & Architecture',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. Amitav Ghosh'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 3,
    subjectCode: 'CS-204',
    subjectName: 'Operating Systems & System Calls',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Rajesh Nair'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 4,
    subjectCode: 'CS-201',
    subjectName: 'Data Structures (Algorithm Analysis)',
    type: 'Tutorial',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. K. Ramanathan'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 5,
    subjectCode: 'EC-202',
    subjectName: 'Digital Electronics & Logic Design',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Naveen Chawla'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 6,
    subjectCode: 'MA-201',
    subjectName: 'Discrete Mathematics & Graph Theory',
    type: 'Theory',
    room: 'LH-204 (Block A)',
    teacherName: 'Dr. S. K. Mahapatra'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 7,
    subjectCode: 'CS-205L',
    subjectName: 'OOP Java Programming Lab',
    type: 'Lab',
    room: 'Software Lab 3',
    teacherName: 'Prof. Ananya Roy'
  },
  {
    dayOfWeek: 'WEDNESDAY',
    period: 8,
    subjectCode: 'CS-205L',
    subjectName: 'OOP Java Programming Lab (Hands-on)',
    type: 'Lab',
    room: 'Software Lab 3',
    teacherName: 'Prof. Ananya Roy'
  },

  // THURSDAY
  {
    dayOfWeek: 'THURSDAY',
    period: 1,
    subjectCode: 'CS-202',
    subjectName: 'Database Management Systems',
    type: 'Theory',
    room: 'LH-102 (Block C)',
    teacherName: 'Prof. Shalini Verma'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 2,
    subjectCode: 'CS-205',
    subjectName: 'Object Oriented Programming (Java)',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Prof. Ananya Roy'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 3,
    subjectCode: 'CS-203',
    subjectName: 'Computer Organization & Architecture',
    type: 'Tutorial',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. Amitav Ghosh'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 4,
    subjectCode: 'CS-204',
    subjectName: 'Operating Systems (Unix Shell Scripting)',
    type: 'Theory',
    room: 'LH-103 (Block C)',
    teacherName: 'Prof. Rajesh Nair'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 5,
    subjectCode: 'CS-207',
    subjectName: 'Web Technologies & Cloud Fundamentals',
    type: 'Theory',
    room: 'LH-102 (Block C)',
    teacherName: 'Prof. Preeti Saxena'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 6,
    subjectCode: 'EC-202L',
    subjectName: 'Digital Circuits & Microprocessor Lab',
    type: 'Lab',
    room: 'Hardware Lab 1 (EC Block)',
    teacherName: 'Prof. Naveen Chawla'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 7,
    subjectCode: 'EC-202L',
    subjectName: 'Digital Circuits & Microprocessor Lab (Cont)',
    type: 'Lab',
    room: 'Hardware Lab 1 (EC Block)',
    teacherName: 'Prof. Naveen Chawla'
  },
  {
    dayOfWeek: 'THURSDAY',
    period: 8,
    subjectCode: 'EC-202L',
    subjectName: 'Digital Circuits Lab (Viva & Verification)',
    type: 'Lab',
    room: 'Hardware Lab 1 (EC Block)',
    teacherName: 'Prof. Naveen Chawla'
  },

  // FRIDAY
  {
    dayOfWeek: 'FRIDAY',
    period: 1,
    subjectCode: 'MA-201',
    subjectName: 'Discrete Mathematics & Graph Theory',
    type: 'Theory',
    room: 'LH-204 (Block A)',
    teacherName: 'Dr. S. K. Mahapatra'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 2,
    subjectCode: 'CS-201',
    subjectName: 'Data Structures & Algorithms',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Dr. K. Ramanathan'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 3,
    subjectCode: 'CS-205',
    subjectName: 'Object Oriented Programming (Java)',
    type: 'Theory',
    room: 'LH-101 (Block C)',
    teacherName: 'Prof. Ananya Roy'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 4,
    subjectCode: 'CS-207',
    subjectName: 'Web Technologies & Cloud Fundamentals',
    type: 'Theory',
    room: 'LH-102 (Block C)',
    teacherName: 'Prof. Preeti Saxena'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 5,
    subjectCode: 'CS-208P',
    subjectName: 'B.Tech Mini-Project & Technical Seminar',
    type: 'Project',
    room: 'Seminar Hall A',
    teacherName: 'Faculty Mentors'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 6,
    subjectCode: 'CS-208P',
    subjectName: 'B.Tech Mini-Project Sprint Review',
    type: 'Project',
    room: 'Seminar Hall A',
    teacherName: 'Faculty Mentors'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 7,
    subjectCode: 'PE-201',
    subjectName: 'Sports, Fitness & Co-Curricular Club',
    type: 'Activity',
    room: 'Sports Complex',
    teacherName: 'Physical Director'
  },
  {
    dayOfWeek: 'FRIDAY',
    period: 8,
    subjectCode: 'PE-201',
    subjectName: 'Student Club & Innovation Hub',
    type: 'Activity',
    room: 'Innovation Hub',
    teacherName: 'Club Mentors'
  },

  // SATURDAY
  {
    dayOfWeek: 'SATURDAY',
    period: 1,
    subjectCode: 'CS-207L',
    subjectName: 'Full Stack Web Dev & Cloud Lab',
    type: 'Lab',
    room: 'Cloud Innovation Lab',
    teacherName: 'Prof. Preeti Saxena'
  },
  {
    dayOfWeek: 'SATURDAY',
    period: 2,
    subjectCode: 'CS-207L',
    subjectName: 'Full Stack Web Dev & Cloud Lab (Cont)',
    type: 'Lab',
    room: 'Cloud Innovation Lab',
    teacherName: 'Prof. Preeti Saxena'
  },
  {
    dayOfWeek: 'SATURDAY',
    period: 3,
    subjectCode: 'CS-REM',
    subjectName: 'Remedial Class & Doubt Resolution',
    type: 'Tutorial',
    room: 'LH-101 (Block C)',
    teacherName: 'Course Faculty'
  },
  {
    dayOfWeek: 'SATURDAY',
    period: 4,
    subjectCode: 'CODE-HUB',
    subjectName: 'Competitive Coding & Algorithmic Hackathon',
    type: 'Seminar',
    room: 'Computer Center (Floor 3)',
    teacherName: 'Coding Club Leads'
  }
];

const ClassTimetable = () => {
  const [timetable, setTimetable] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDay, setSelectedDay] = useState(getTodayDayName());
  const [viewMode, setViewMode] = useState('grid'); // 'grid' | 'day'

  function getTodayDayName() {
    const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const todayStr = days[new Date().getDay()];
    return DAYS.includes(todayStr) ? todayStr : 'MONDAY';
  }

  useEffect(() => {
    fetchTimetable();
  }, []);

  const fetchTimetable = async () => {
    setLoading(true);
    try {
      const res = await api.get('/timetable');
      if (res.data && Array.isArray(res.data) && res.data.length > 0) {
        setTimetable(res.data);
      } else {
        // Load default B.Tech engineering timetable
        setTimetable(BTECH_SAMPLE_TIMETABLE);
      }
    } catch (err) {
      // Fallback seamlessly to B.Tech Sample Timetable
      setTimetable(BTECH_SAMPLE_TIMETABLE);
    } finally {
      setLoading(false);
    }
  };

  const getEntry = (day, periodNum) => {
    return timetable.find(t => t.dayOfWeek === day && t.period === periodNum);
  };

  const getDaySchedule = (day) => {
    return PERIOD_INFO.map(p => {
      const entry = getEntry(day, p.period);
      return {
        ...p,
        entry
      };
    });
  };

  const handlePrint = () => {
    window.print();
  };

  const getTypeBadgeClass = (type) => {
    if (!type) return 'type-lecture';
    const lower = type.toLowerCase();
    if (lower.includes('lab') || lower.includes('practical')) return 'type-lab';
    if (lower.includes('tutorial')) return 'type-tutorial';
    if (lower.includes('project') || lower.includes('seminar')) return 'type-project';
    if (lower.includes('activity') || lower.includes('sports')) return 'type-activity';
    return 'type-lecture';
  };

  if (loading) {
    return (
      <div style={{ padding: '3rem', textAlign: 'center' }}>
        <Spinner label="Loading B.Tech class timetable..." />
      </div>
    );
  }

  const todayClasses = getDaySchedule(selectedDay);
  const todayActiveCount = todayClasses.filter(c => c.entry).length;

  return (
    <div className="timetable-container">
      {/* B.Tech Engineering Header Banner */}
      <div className="btech-info-card">
        <div className="btech-info-left">
          <div className="btech-badge">
            <Sparkles size={14} /> B.Tech Sample Curriculum
          </div>
          <h2 className="btech-program-title">
            Bachelor of Technology (B.Tech) &bull; Computer Science & Engineering
          </h2>
          <p className="btech-meta">
            <span><strong>Semester:</strong> IV (Year 2)</span>
            <span>&bull;</span>
            <span><strong>Section:</strong> CSE-B</span>
            <span>&bull;</span>
            <span><strong>Room:</strong> LH-101 (Block C)</span>
            <span>&bull;</span>
            <span><strong>Academic Year:</strong> 2024–2025</span>
          </p>
        </div>

        <div className="btech-info-right">
          <button
            type="button"
            className="timetable-action-btn"
            onClick={handlePrint}
            title="Print or Save Timetable as PDF"
          >
            <Printer size={16} /> Print Timetable
          </button>
          <div className="view-mode-toggle">
            <button
              type="button"
              className={`toggle-tab ${viewMode === 'grid' ? 'active' : ''}`}
              onClick={() => setViewMode('grid')}
            >
              Weekly Grid
            </button>
            <button
              type="button"
              className={`toggle-tab ${viewMode === 'day' ? 'active' : ''}`}
              onClick={() => setViewMode('day')}
            >
              Day Agenda
            </button>
          </div>
        </div>
      </div>

      {/* Day Selector Chips */}
      <div className="timetable-controls">
        <div className="day-chips">
          {DAYS.map(day => (
            <button
              key={day}
              type="button"
              className={`day-chip ${selectedDay === day ? 'active' : ''}`}
              onClick={() => setSelectedDay(day)}
            >
              {day.slice(0, 3)}
              {getTodayDayName() === day && <span className="today-dot" title="Today" />}
            </button>
          ))}
        </div>

        <div className="legend-pills">
          <span className="legend-pill type-lecture"><span className="dot" /> Lecture</span>
          <span className="legend-pill type-lab"><span className="dot" /> Lab / Practical</span>
          <span className="legend-pill type-tutorial"><span className="dot" /> Tutorial</span>
          <span className="legend-pill type-project"><span className="dot" /> Project / Seminar</span>
        </div>
      </div>

      {/* VIEW MODE 1: WEEKLY FULL GRID */}
      {viewMode === 'grid' && (
        <div className="timetable-grid-card">
          <div className="timetable-table-wrapper">
            <table className="timetable-table">
              <thead>
                <tr>
                  <th className="period-header-col">PERIOD / TIME</th>
                  {DAYS.map(day => (
                    <th
                      key={day}
                      className={`day-header-col ${selectedDay === day ? 'highlight' : ''}`}
                    >
                      <div className="day-header-name">{day}</div>
                      {getTodayDayName() === day && <span className="day-today-tag">TODAY</span>}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {PERIOD_INFO.map(periodInfo => {
                  const isLunchBreakSlot = periodInfo.period === 5;
                  return (
                    <React.Fragment key={periodInfo.period}>
                      {isLunchBreakSlot && (
                        <tr className="lunch-break-row">
                          <td colSpan={DAYS.length + 1} className="lunch-break-cell">
                            <Clock size={13} /> LUNCH BREAK (12:40 PM – 01:30 PM)
                          </td>
                        </tr>
                      )}
                      <tr>
                        <td className="period-cell">
                          <div className="period-badge">{periodInfo.label}</div>
                          <div className="period-time">{periodInfo.time}</div>
                        </td>

                        {DAYS.map(day => {
                          const entry = getEntry(day, periodInfo.period);
                          const isSelectedDay = selectedDay === day;
                          return (
                            <td
                              key={day}
                              className={`timetable-cell ${isSelectedDay ? 'day-highlight' : ''}`}
                            >
                              {entry ? (
                                <div className={`subject-card ${getTypeBadgeClass(entry.type)}`}>
                                  <div className="subject-code-row">
                                    <Badge variant="indigo">{entry.subjectCode}</Badge>
                                    {entry.type && (
                                      <span className={`entry-type-pill ${getTypeBadgeClass(entry.type)}`}>
                                        {entry.type}
                                      </span>
                                    )}
                                  </div>

                                  <div className="subject-name" title={entry.subjectName}>
                                    {entry.subjectName}
                                  </div>

                                  <div className="subject-footer">
                                    <span className="room-pill" title={entry.room}>
                                      <MapPin size={11} /> {entry.room || 'LH-101'}
                                    </span>
                                    {entry.teacherName && (
                                      <span className="teacher-pill" title={entry.teacherName}>
                                        <User size={11} /> {entry.teacherName}
                                      </span>
                                    )}
                                  </div>
                                </div>
                              ) : (
                                <div className="free-period">
                                  <span>Free / Self-Study</span>
                                </div>
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    </React.Fragment>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* VIEW MODE 2: DAY AGENDA */}
      {viewMode === 'day' && (
        <div className="day-agenda-container">
          <div className="day-agenda-header">
            <div>
              <h3 className="day-agenda-title">
                {selectedDay} Schedule
                {getTodayDayName() === selectedDay && <span className="today-badge">TODAY</span>}
              </h3>
              <p className="day-agenda-sub">
                {todayActiveCount} active engineering classes scheduled for {selectedDay}.
              </p>
            </div>
          </div>

          <div className="day-timeline-list">
            {todayClasses.map((item) => (
              <div
                key={item.period}
                className={`timeline-row ${item.entry ? 'has-class' : 'is-free'}`}
              >
                <div className="timeline-time-col">
                  <span className="timeline-period-badge">{item.label}</span>
                  <span className="timeline-clock-label">{item.time}</span>
                </div>

                <div className="timeline-card-col">
                  {item.entry ? (
                    <div className={`agenda-card ${getTypeBadgeClass(item.entry.type)}`}>
                      <div className="agenda-card-top">
                        <div className="agenda-subject-group">
                          <span className="agenda-code">{item.entry.subjectCode}</span>
                          <span className="agenda-name">{item.entry.subjectName}</span>
                        </div>
                        {item.entry.type && (
                          <span className={`entry-type-pill ${getTypeBadgeClass(item.entry.type)}`}>
                            {item.entry.type}
                          </span>
                        )}
                      </div>

                      <div className="agenda-details-row">
                        <span className="agenda-detail">
                          <MapPin size={13} /> {item.entry.room}
                        </span>
                        {item.entry.teacherName && (
                          <span className="agenda-detail">
                            <User size={13} /> {item.entry.teacherName}
                          </span>
                        )}
                      </div>
                    </div>
                  ) : (
                    <div className="agenda-free-card">
                      <Clock size={14} /> Free Period / Project Work & Self Study
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Timetable Notes / Footer Guide */}
      <div className="timetable-footer-notes">
        <Info size={16} className="info-icon" />
        <div className="notes-text">
          <strong>B.Tech Academic Regulation Notes:</strong> 75% minimum attendance is mandatory across all theory and laboratory courses to qualify for end-semester examinations. Laboratory sessions require submission of recorded experiments within 48 hours.
        </div>
      </div>
    </div>
  );
};

export default ClassTimetable;
