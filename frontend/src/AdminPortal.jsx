import { useEffect, useState } from 'react';
import './PatientAppointments.css';

const api = 'http://localhost:8081/api/admin';
const getToken = () => localStorage.getItem('medislot_token') || '';

function AdminPortal({ onLogin }) {
  const [admin, setAdmin] = useState(null);
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [dashboard, setDashboard] = useState(null);
  const [patients, setPatients] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [newDoctor, setNewDoctor] = useState({ name: '', specialization: '', email: '' });
  const [appointmentStatuses, setAppointmentStatuses] = useState({});
  const headers = () => ({ 'Content-Type': 'application/json', Authorization: `Bearer ${getToken()}` });
  const load = async () => {
    const paths = ['dashboard','patients','doctors','appointments','analytics'];
    const responses = await Promise.all(paths.map(path => fetch(`${api}/${path}`, { headers: headers() })));
    if (responses.some(response => !response.ok)) throw new Error('Could not load admin data. Check your account permissions.');
    const [d,p,doc,a,stats] = await Promise.all(responses.map(response => response.json()));
    setDashboard(d); setPatients(p); setDoctors(doc); setAppointments(a); setAnalytics(stats); setAdmin(true);
  };
  useEffect(() => { if (getToken()) load().catch(err => setError(err.message)); }, []);
  const login = async event => {
    event.preventDefault(); setError('');
    try {
      const response = await fetch(`${api}/login`, { method: 'POST', headers: { 'Content-Type':'application/json' }, body: JSON.stringify(credentials) });
      const data=await response.json(); if(!response.ok) throw new Error(data.error || data.message || 'Admin login failed.');
      localStorage.setItem('medislot_token',data.token); onLogin({ name: data.email, role: 'ADMIN' });
    } catch(err) { setError(err.message); }
  };
  const setActive = async (kind,id,active) => {
    try { const response=await fetch(`${api}/${kind}/${id}/active`,{method:'PATCH',headers:headers(),body:JSON.stringify({active})}); if(!response.ok) throw new Error('Unable to update account.'); await load(); }
    catch(err) { setError(err.message); }
  };
  const addDoctor = async event => {
    event.preventDefault(); setError('');
    try { const response=await fetch(`${api}/doctors`,{method:'POST',headers:headers(),body:JSON.stringify(newDoctor)}); const data=await response.json(); if(!response.ok) throw new Error(data.error||data.message||'Could not add doctor.'); setNewDoctor({name:'',specialization:'',email:''}); await load(); }
    catch(err) { setError(err.message); }
  };
  const updateAppointment = async id => {
    const status=appointmentStatuses[id]; if(!status) return;
    try { const response=await fetch(`${api}/appointments/${id}/status`,{method:'PATCH',headers:headers(),body:JSON.stringify({status})}); if(!response.ok) throw new Error('Could not update appointment.'); await load(); }
    catch(err) { setError(err.message); }
  };
  if (!admin) return <main className="appointments-page"><section className="appointments-container appointment-card"><h2>Admin sign in</h2>{error&&<p className="appointment-error" role="alert">{error}</p>}<form onSubmit={login}><label>Email<input type="email" value={credentials.email} onChange={e=>setCredentials({...credentials,email:e.target.value})} required /></label><label>Password<input type="password" value={credentials.password} onChange={e=>setCredentials({...credentials,password:e.target.value})} required /></label><button>Sign in</button></form></section></main>;
  return <main className="appointments-page"><div className="appointments-container"><h2>Admin dashboard</h2>{error&&<p className="appointment-error" role="alert">{error}</p>}
    {dashboard&&<div className="feature-grid">{Object.entries(dashboard).map(([label,value])=><article className="feature-card" key={label}><strong>{label.replace(/[A-Z]/g,c=>` ${c}`).toUpperCase()}</strong><h3>{value}</h3></article>)}</div>}
    <section><h3>Patients</h3>{patients.map(item=><article className="appointment-card" key={item.id}><strong>{item.name}</strong> · {item.email} · {item.active?'Active':'Inactive'} <button onClick={()=>setActive('patients',item.id,!item.active)}>{item.active?'Deactivate':'Activate'}</button></article>)}</section>
    <section><h3>Doctors</h3><form className="appointment-card" onSubmit={addDoctor}><h4>Add doctor profile</h4>{['name','specialization','email'].map(key=><label key={key}>{key}<input type={key==='email'?'email':'text'} value={newDoctor[key]} onChange={e=>setNewDoctor({...newDoctor,[key]:e.target.value})} required /></label>)}<button>Add doctor</button></form>{doctors.map(item=><article className="appointment-card" key={item.id}><strong>{item.name}</strong> · {item.specialization} · {item.active?'Active':'Inactive'} <button onClick={()=>setActive('doctors',item.id,!item.active)}>{item.active?'Deactivate':'Activate'}</button></article>)}</section>
    <section><h3>Appointments</h3>{appointments.map(item=><article className="appointment-card" key={item.id}><strong>#{item.id} {item.patientName}</strong> with {item.doctorName} · {item.appointmentDate} {item.appointmentTime} · {item.status}<label>Update status <select value={appointmentStatuses[item.id] || item.status} onChange={e=>setAppointmentStatuses({...appointmentStatuses,[item.id]:e.target.value})}><option>CONFIRMED</option><option>CHECKED_IN</option><option>IN_CONSULTATION</option><option>COMPLETED</option><option>CANCELLED</option></select></label><button onClick={()=>updateAppointment(item.id)}>Save status</button></article>)}</section>
    <section><h3>Analytics</h3>{analytics&&Object.entries(analytics).filter(([,value])=>typeof value==='object').map(([label,values])=>{const max=Math.max(1,...Object.values(values));return <article className="appointment-card" key={label}><h4>{label}</h4>{Object.entries(values).map(([key,value])=><div key={key} aria-label={`${key}: ${value}`}><small>{key} · {value}</small><div style={{height:8,width:`${Math.max(2,(value/max)*100)}%`,background:'#238b78',borderRadius:6,margin:'4px 0 10px'}} /></div>)}</article>})}</section>
  </div></main>;
}
export default AdminPortal;
