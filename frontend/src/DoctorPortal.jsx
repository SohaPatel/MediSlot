import { useEffect, useState } from 'react';
import './PatientAppointments.css';

const root = 'http://localhost:8081/api/doctors';
const token = () => localStorage.getItem('medislot_token') || '';

function DoctorPortal({ onLogin }) {
  const [registering, setRegistering] = useState(false);
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [registration, setRegistration] = useState({ name: '', specialization: '', email: '', password: '', registrationCode: '' });
  const [error, setError] = useState('');
  const [doctor, setDoctor] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [slotsText, setSlotsText] = useState('');
  const [range, setRange] = useState('today');
  const [message, setMessage] = useState('');
  const [profileForm, setProfileForm] = useState({ name: '', specialization: '', consultationDurationMinutes: 30 });
  const [blockedDate, setBlockedDate] = useState('');
  const [prescriptionFor, setPrescriptionFor] = useState(null);
  const [prescriptionDraft, setPrescriptionDraft] = useState({ medicineName: '', dosage: '', frequency: '', duration: '', instructions: '' });
  const [emergencyRequests, setEmergencyRequests] = useState([]);
  const [clinicalPriority, setClinicalPriority] = useState({});
  const [clinicalNotes, setClinicalNotes] = useState({});

  const headers = () => ({ 'Content-Type': 'application/json', Authorization: `Bearer ${token()}` });
  const loadDashboard = async (selectedRange = range) => {
    const [profileResponse, appointmentResponse, emergencyResponse] = await Promise.all([
      fetch(`${root}/me`, { headers: headers() }),
      fetch(`${root}/me/appointments?range=${selectedRange}`, { headers: headers() }),
      fetch(`${root}/me/emergency-requests`, { headers: headers() })
    ]);
    if (!profileResponse.ok || !appointmentResponse.ok || !emergencyResponse.ok) throw new Error('Could not load doctor dashboard.');
    const profile = await profileResponse.json();
    setDoctor(profile);
    setProfileForm({ name: profile.name, specialization: profile.specialization, consultationDurationMinutes: profile.consultationDurationMinutes || 30 });
    setSlotsText((profile.availableSlots || []).join(', '));
    setAppointments(await appointmentResponse.json());
    setEmergencyRequests(await emergencyResponse.json());
  };

  useEffect(() => { if (token()) loadDashboard().catch(err => setError(err.message)); }, []);
  useEffect(() => { if (doctor) loadDashboard(range).catch(err => setError(err.message)); }, [range]);

  const login = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${root}/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(credentials) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Login failed.');
      localStorage.setItem('medislot_token', data.token);
      onLogin({ ...data.doctor, role: 'DOCTOR' });
    } catch (err) { setError(err.message); }
  };

  const register = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${root}/register`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(registration) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Registration failed.');
      setRegistering(false); setCredentials({ email: registration.email, password: '' });
      setMessage('Doctor profile created. Sign in to continue.');
    } catch (err) { setError(err.message); }
  };

  const saveAvailability = async event => {
    event.preventDefault(); setError(''); setMessage('');
    try {
      const slots = slotsText.split(',').map(item => item.trim()).filter(Boolean);
      const response = await fetch(`${root}/me/availability`, { method: 'PUT', headers: headers(), body: JSON.stringify(slots) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to save availability.');
      setDoctor(data); setSlotsText(data.availableSlots.join(', ')); setMessage('Availability updated.');
    } catch (err) { setError(err.message); }
  };

  const saveProfile = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${root}/me`, { method: 'PUT', headers: headers(), body: JSON.stringify(profileForm) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to update profile.');
      setDoctor(data); setMessage('Profile updated.');
    } catch (err) { setError(err.message); }
  };

  const blockDate = async event => {
    event.preventDefault(); setError('');
    try {
      const dates = [...new Set([...(doctor.blockedDates || []), blockedDate])];
      const response = await fetch(`${root}/me/blocked-dates`, { method: 'PUT', headers: headers(), body: JSON.stringify(dates) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to block date.');
      setDoctor(data); setBlockedDate(''); setMessage('Date blocked.');
    } catch (err) { setError(err.message); }
  };

  const updateStatus = async (id, status) => {
    setError('');
    try {
      const response = await fetch(`${root}/me/appointments/${id}/status`, { method: 'PATCH', headers: headers(), body: JSON.stringify({ status }) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to update appointment.');
      await loadDashboard();
    } catch (err) { setError(err.message); }
  };

  const createPrescription = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${root}/me/appointments/${prescriptionFor}/prescriptions`, { method: 'POST', headers: headers(), body: JSON.stringify(prescriptionDraft) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to save prescription.');
      setPrescriptionFor(null); setPrescriptionDraft({ medicineName: '', dosage: '', frequency: '', duration: '', instructions: '' });
      setMessage(`Prescription #${data.id} created.`);
    } catch (err) { setError(err.message); }
  };

  const reviewEmergency = async (id, decision) => {
    setError('');
    try {
      const response = await fetch(`${root}/me/emergency-requests/${id}/review`, { method: 'PATCH', headers: headers(), body: JSON.stringify({ decision, clinicalPriority: Number(clinicalPriority[id] || 1), clinicalNotes: clinicalNotes[id] || '' }) });
      const data=await response.json(); if(!response.ok) throw new Error(data.error || data.message || 'Unable to review request.');
      setEmergencyRequests(previous=>previous.filter(item=>item.id!==id)); setMessage(`Clinical review recorded: ${data.status}.`);
    } catch(err) { setError(err.message); }
  };

  if (!doctor) return <main className="appointments-page"><div className="appointments-container"><section className="appointment-card">
    <h2>{registering ? 'Doctor registration' : 'Doctor sign in'}</h2>
    {error && <p className="appointment-error" role="alert">{error}</p>}{message && <p role="status">{message}</p>}
    {registering ? <form onSubmit={register}>{['name','specialization','email','password','registrationCode'].map(key => <label key={key}>{key === 'registrationCode' ? 'Onboarding code' : key}<input type={key === 'password' || key === 'registrationCode' ? 'password' : key === 'email' ? 'email' : 'text'} value={registration[key]} onChange={e => setRegistration({ ...registration, [key]: e.target.value })} required minLength={key === 'password' ? 12 : undefined} /></label>)}<button>Create doctor account</button></form> : <form onSubmit={login}><label>Email<input type="email" value={credentials.email} onChange={e => setCredentials({ ...credentials, email: e.target.value })} required /></label><label>Password<input type="password" value={credentials.password} onChange={e => setCredentials({ ...credentials, password: e.target.value })} required /></label><button>Sign in</button></form>}
    <button type="button" onClick={() => { setRegistering(!registering); setError(''); }}>{registering ? 'Back to sign in' : 'Register as doctor'}</button>
  </section></div></main>;

  return <main className="appointments-page"><div className="appointments-container"><h2>Doctor dashboard</h2><p>Welcome, {doctor.name} · {doctor.specialization}</p>
    {error && <p className="appointment-error" role="alert">{error}</p>}{message && <p className="cancellation-success" role="status">{message}</p>}
    <section className="appointment-card"><h3>Profile</h3><form onSubmit={saveProfile}><label>Name<input value={profileForm.name} onChange={e => setProfileForm({ ...profileForm, name: e.target.value })} required /></label><label>Specialization<input value={profileForm.specialization} onChange={e => setProfileForm({ ...profileForm, specialization: e.target.value })} required /></label><label>Consultation duration (minutes)<input type="number" min="10" max="180" value={profileForm.consultationDurationMinutes} onChange={e => setProfileForm({ ...profileForm, consultationDurationMinutes: Number(e.target.value) })} required /></label><button>Save profile</button></form></section>
    <section className="appointment-card"><h3>Availability</h3><form onSubmit={saveAvailability}><label>Available time slots, comma separated (24-hour time)<input value={slotsText} onChange={e => setSlotsText(e.target.value)} placeholder="09:00, 09:30, 10:00" /></label><button>Save availability</button></form><form onSubmit={blockDate}><label>Block a date<input type="date" value={blockedDate} onChange={e => setBlockedDate(e.target.value)} required /></label><button>Block date</button></form><p>Blocked dates: {(doctor.blockedDates || []).join(', ') || 'None'}</p></section>
    <section><h3>Clinical review requests</h3>{emergencyRequests.length===0?<p>No requests awaiting review.</p>:emergencyRequests.map(item=><article className="appointment-card" key={item.id}><h4>Request #{item.id} · {item.specialization} · {item.requestedDate}</h4><p>Patient #{item.patientId}: {item.reason}</p><label>Clinical priority after assessment (1–5)<select value={clinicalPriority[item.id]||1} onChange={e=>setClinicalPriority({...clinicalPriority,[item.id]:e.target.value})}>{[1,2,3,4,5].map(value=><option key={value}>{value}</option>)}</select></label><label>Clinical notes<textarea maxLength="2000" value={clinicalNotes[item.id]||''} onChange={e=>setClinicalNotes({...clinicalNotes,[item.id]:e.target.value})} required /></label><button onClick={()=>reviewEmergency(item.id,'APPROVE')}>Approve priority</button><button onClick={()=>reviewEmergency(item.id,'DENY')}>Deny request</button></article>)}</section>
    <label>Appointments <select value={range} onChange={e => setRange(e.target.value)}><option value="today">Today</option><option value="upcoming">Upcoming</option><option value="completed">Completed</option><option value="cancelled">Cancelled</option><option value="all">All</option></select></label>
    {appointments.map(item => <article className="appointment-card" key={item.id}><h3>{item.patientName}</h3><p>{item.appointmentDate} · {item.appointmentTime} · {item.status}</p>{item.status !== 'CANCELLED' && item.status !== 'COMPLETED' && <div><button onClick={() => updateStatus(item.id, 'CHECKED_IN')}>Check in</button><button onClick={() => updateStatus(item.id, 'IN_CONSULTATION')}>Start consultation</button><button onClick={() => updateStatus(item.id, 'COMPLETED')}>Complete</button></div>}{['IN_CONSULTATION','COMPLETED'].includes(item.status)&&<button onClick={()=>setPrescriptionFor(item.id)}>Write prescription</button>}{prescriptionFor===item.id&&<form onSubmit={createPrescription}><h4>Digital prescription</h4>{['medicineName','dosage','frequency','duration','instructions'].map(key=><label key={key}>{key}<input value={prescriptionDraft[key]} onChange={e=>setPrescriptionDraft({...prescriptionDraft,[key]:e.target.value})} required={key!=='instructions'} /></label>)}<button>Save prescription</button><button type="button" onClick={()=>setPrescriptionFor(null)}>Cancel</button></form>}</article>)}
  </div></main>;
}
export default DoctorPortal;
