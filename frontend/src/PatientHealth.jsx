import { useEffect, useState } from 'react';
import './PatientAppointments.css';

const API = 'http://localhost:8081/api/patients/me';
const authHeaders = () => ({ 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('medislot_token') || ''}` });

function PatientHealth() {
  const [profile, setProfile] = useState({ name: '', phone: '', age: '' });
  const [history, setHistory] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [emergencyRequests, setEmergencyRequests] = useState([]);
  const [emergencyDraft, setEmergencyDraft] = useState({ specialization: '', requestedDate: '', reason: '' });
  const [entry, setEntry] = useState({ title: '', details: '', allergies: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const load = async () => {
    const [profileResponse, historyResponse, prescriptionResponse, notificationResponse, emergencyResponse] = await Promise.all([
      fetch(API, { headers: authHeaders() }),
      fetch(`${API}/medical-history`, { headers: authHeaders() }),
      fetch(`${API}/prescriptions`, { headers: authHeaders() }),
      fetch(`${API}/notifications`, { headers: authHeaders() }),
      fetch(`${API}/emergency-requests`, { headers: authHeaders() })
    ]);
    if (!profileResponse.ok || !historyResponse.ok || !prescriptionResponse.ok || !notificationResponse.ok || !emergencyResponse.ok) throw new Error('Unable to load your health information.');
    const data = await profileResponse.json();
    setProfile({ name: data.name, phone: data.phone, age: data.age ?? '' });
    setHistory(await historyResponse.json());
    setPrescriptions(await prescriptionResponse.json());
    setNotifications(await notificationResponse.json());
    setEmergencyRequests(await emergencyResponse.json());
  };

  useEffect(() => { load().catch(err => setError(err.message)); }, []);

  const saveProfile = async event => {
    event.preventDefault(); setError(''); setMessage('');
    try {
      const response = await fetch(API, { method: 'PUT', headers: authHeaders(), body: JSON.stringify({ ...profile, age: Number(profile.age) }) });
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || 'Unable to update profile.');
      setMessage('Profile updated.');
    } catch (err) { setError(err.message); }
  };

  const addHistory = async event => {
    event.preventDefault(); setError(''); setMessage('');
    try {
      const response = await fetch(`${API}/medical-history`, { method: 'POST', headers: authHeaders(), body: JSON.stringify(entry) });
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || 'Unable to save medical history.');
      setHistory(previous => [data, ...previous]);
      setEntry({ title: '', details: '', allergies: '' });
      setMessage('Medical history saved.');
    } catch (err) { setError(err.message); }
  };

  const downloadPrescription = async id => {
    try {
      const response = await fetch(`${API}/prescriptions/${id}.pdf`, { headers: authHeaders() });
      if (!response.ok) throw new Error('Unable to download prescription.');
      const url = URL.createObjectURL(await response.blob()); const link = document.createElement('a');
      link.href = url; link.download = `medislot-prescription-${id}.pdf`; link.click(); URL.revokeObjectURL(url);
    } catch (err) { setError(err.message); }
  };

  const markRead = async id => {
    const response = await fetch(`${API}/notifications/${id}/read`, { method: 'PATCH', headers: authHeaders() });
    if (response.ok) setNotifications(previous => previous.map(item => item.id === id ? { ...item, readAt: new Date().toISOString() } : item));
  };

  const requestClinicalReview = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${API}/emergency-requests`, { method: 'POST', headers: authHeaders(), body: JSON.stringify(emergencyDraft) });
      const data = await response.json(); if (!response.ok) throw new Error(data.error || data.message || 'Unable to request clinical review.');
      setEmergencyRequests(previous => [data, ...previous]); setEmergencyDraft({ specialization: '', requestedDate: '', reason: '' });
    } catch (err) { setError(err.message); }
  };

  return <main className="appointments-page"><div className="appointments-container">
    <h2>Profile and Medical History</h2>
    {error && <p className="appointment-error" role="alert">{error}</p>}
    {message && <p className="cancellation-success" role="status">{message}</p>}
    <section className="appointment-card"><h3>Personal details</h3>
      <form onSubmit={saveProfile}>
        <label>Full name <input value={profile.name} onChange={e => setProfile({ ...profile, name: e.target.value })} minLength="2" maxLength="100" required /></label>
        <label>Phone <input type="tel" value={profile.phone} onChange={e => setProfile({ ...profile, phone: e.target.value })} pattern="[0-9]{10}" required /></label>
        <label>Age <input type="number" value={profile.age} onChange={e => setProfile({ ...profile, age: e.target.value })} min="1" max="120" required /></label>
        <button type="submit">Save profile</button>
      </form>
    </section>
    <section className="appointment-card"><h3>Add medical history</h3>
      <form onSubmit={addHistory}>
        <label>Title <input value={entry.title} onChange={e => setEntry({ ...entry, title: e.target.value })} maxLength="120" required /></label>
        <label>Details <textarea value={entry.details} onChange={e => setEntry({ ...entry, details: e.target.value })} maxLength="4000" required /></label>
        <label>Allergies <textarea value={entry.allergies} onChange={e => setEntry({ ...entry, allergies: e.target.value })} maxLength="2000" /></label>
        <button type="submit">Save health information</button>
      </form>
    </section>
    <section><h3>Your medical history</h3>{history.length === 0 ? <p>No medical history has been added.</p> : history.map(item => <article className="appointment-card" key={item.id}><h4>{item.title}</h4><p>{item.details}</p>{item.allergies && <p><strong>Allergies:</strong> {item.allergies}</p>}<small>{item.createdAt}</small></article>)}</section>
    <section><h3>Prescriptions</h3>{prescriptions.length===0?<p>No prescriptions available.</p>:prescriptions.map(item=><article className="appointment-card" key={item.id}><h4>{item.medicineName}</h4><p>{item.dosage} · {item.frequency} · {item.duration}</p><p>Prescribed by {item.doctorName} on {item.prescriptionDate}</p>{item.instructions&&<p>{item.instructions}</p>}<button type="button" onClick={()=>downloadPrescription(item.id)}>Download PDF</button></article>)}</section>
    <section><h3>Notifications</h3>{notifications.length===0?<p>No notifications.</p>:notifications.map(item=><article className="appointment-card" key={item.id}><p>{item.message}</p><small>{item.createdAt} · {item.readAt?'Read':'Unread'}</small>{!item.readAt&&<button type="button" onClick={()=>markRead(item.id)}>Mark as read</button>}</article>)}</section>
    <section className="appointment-card"><h3>Request urgent clinical review</h3><p>A licensed clinician will review the request. MediSlot does not diagnose or assign emergency priority automatically.</p><form onSubmit={requestClinicalReview}><label>Specialization<input value={emergencyDraft.specialization} onChange={e=>setEmergencyDraft({...emergencyDraft,specialization:e.target.value})} required /></label><label>Preferred date<input type="date" min={new Date().toLocaleDateString('en-CA')} value={emergencyDraft.requestedDate} onChange={e=>setEmergencyDraft({...emergencyDraft,requestedDate:e.target.value})} required /></label><label>Reason for review<textarea maxLength="2000" value={emergencyDraft.reason} onChange={e=>setEmergencyDraft({...emergencyDraft,reason:e.target.value})} required /></label><button>Submit for clinician review</button></form>{emergencyRequests.map(item=><p key={item.id}>Request #{item.id} · {item.status} · {item.requestedDate}{item.clinicalPriority>0?` · clinician priority ${item.clinicalPriority}`:''}</p>)}</section>
  </div></main>;
}

export default PatientHealth;
