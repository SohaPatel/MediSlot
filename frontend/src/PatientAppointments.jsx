
import { useEffect, useState } from 'react';
import './PatientAppointments.css';

function PatientAppointments({ patientId = 1 }) {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [cancelError, setCancelError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [cancellingId, setCancellingId] = useState(null);
    const [doctors, setDoctors] = useState([]);
    const [reschedulingId, setReschedulingId] = useState(null);
    const [rescheduleDate, setRescheduleDate] = useState('');
    const [rescheduleTime, setRescheduleTime] = useState('');
    const [waitingEntries, setWaitingEntries] = useState([]);
    const [reviewDrafts, setReviewDrafts] = useState({});
    const [reviewedIds, setReviewedIds] = useState([]);
    const [queueStatuses, setQueueStatuses] = useState({});

    useEffect(() => {
        const fetchAppointments = async () => {
            try {
                const response = await fetch(
                    `http://localhost:8081/api/appointments/patient/${patientId}`,
                    { headers: { 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` } }
                );

                if (!response.ok) {
                    throw new Error('Unable to load appointments.');
                }

                const data = await response.json();
                setAppointments(data);
                const queueResponse = await fetch(`http://localhost:8081/api/appointments/patient/${patientId}/queue`, {
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` }
                });
                if (queueResponse.ok) {
                    const queue = await queueResponse.json();
                    setQueueStatuses(Object.fromEntries(queue.map(item => [item.appointmentId, item])));
                }
            } catch (err) {
                setError(err.message || 'Unable to connect to server.');
            } finally {
                setLoading(false);
            }
        };

        fetchAppointments();
    }, [patientId]);

    useEffect(() => {
        fetch('http://localhost:8081/api/appointments/doctors')
            .then(response => response.json())
            .then(setDoctors)
            .catch(() => setDoctors([]));
    }, []);

    useEffect(() => {
        fetch('http://localhost:8081/api/appointments/waiting-list', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` }
        }).then(response => response.ok ? response.json() : [])
          .then(setWaitingEntries).catch(() => setWaitingEntries([]));
    }, [patientId]);

    const handleReschedule = async (appointmentId) => {
        setCancelError('');
        setSuccessMessage('');
        try {
            const response = await fetch(
                `http://localhost:8081/api/appointments/${appointmentId}/reschedule`,
                {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}`
                    },
                    body: JSON.stringify({ appointmentDate: rescheduleDate, appointmentTime: rescheduleTime })
                }
            );
            const data = await response.json();
            if (!response.ok) throw new Error(data.error || data.message || 'Unable to reschedule appointment.');
            setAppointments(previous => previous.map(item => item.id === appointmentId ? data : item));
            setSuccessMessage(`Appointment #${appointmentId} rescheduled successfully.`);
            setReschedulingId(null);
        } catch (err) {
            setCancelError(err.message || 'Unable to reschedule appointment.');
        }
    };

    const downloadConfirmation = async (appointmentId) => {
        try {
            const response = await fetch(`http://localhost:8081/api/appointments/${appointmentId}/confirmation.pdf`, {
                headers: { 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` }
            });
            if (!response.ok) throw new Error('Unable to download appointment confirmation.');
            const fileUrl = URL.createObjectURL(await response.blob());
            const link = document.createElement('a');
            link.href = fileUrl;
            link.download = `medislot-appointment-${appointmentId}.pdf`;
            link.click();
            URL.revokeObjectURL(fileUrl);
        } catch (err) {
            setCancelError(err.message || 'Unable to download confirmation.');
        }
    };

    const submitReview = async appointmentId => {
        setCancelError(''); setSuccessMessage('');
        try {
            const draft = reviewDrafts[appointmentId] || { rating: 5, comment: '' };
            const response = await fetch(`http://localhost:8081/api/appointments/${appointmentId}/review`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` },
                body: JSON.stringify({ rating: Number(draft.rating), comment: draft.comment })
            });
            const data = await response.json();
            if (!response.ok) throw new Error(data.error || data.message || 'Unable to submit review.');
            setReviewedIds(previous => [...previous, appointmentId]);
            setSuccessMessage('Thank you for reviewing your doctor.');
        } catch (err) { setCancelError(err.message || 'Unable to submit review.'); }
    };

    // Cancel appointment
    const handleCancelAppointment = async (appointmentId) => {
        if (cancellingId !== null) {
            return;
        }

        const confirmed = window.confirm(
            'Are you sure you want to cancel this appointment?'
        );

        if (!confirmed) {
            return;
        }

        setCancellingId(appointmentId);
        setCancelError('');
        setSuccessMessage('');

        try {
            const response = await fetch(
                `http://localhost:8081/api/appointments/${appointmentId}/cancel`,
                {
                    method: 'PATCH',
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('medislot_token') || ''}` }
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.error || data.message || 'Unable to cancel appointment.'
                );
            }

            // Update appointment status without refreshing the page
            setAppointments((previousAppointments) =>
                previousAppointments.map((appointment) =>
                    appointment.id === appointmentId
                        ? { ...appointment, status: 'CANCELLED' }
                        : appointment
                )
            );

            setSuccessMessage(
                `Appointment #${appointmentId} cancelled successfully!`
            );

        } catch (err) {
            setCancelError(
                err.message || 'Unable to connect to server.'
            );
        } finally {
            setCancellingId(null);
        }
    };

    if (loading) {
        return (
            <div className="appointments-page">
                Loading appointments...
            </div>
        );
    }

    return (
        <div className="appointments-page">
            <div className="appointments-container">

                <h2>My Appointments</h2>

                <p className="appointments-subtitle">
                    View your MediSlot booking history.
                </p>

                {waitingEntries.length > 0 && (
                    <section aria-label="Waiting list updates">
                        <h3>Waiting list</h3>
                        {waitingEntries.map(entry => <p key={entry.id}>
                            {entry.specialization} · {entry.appointmentDate} · {entry.status === 'OFFERED' ? 'A slot was released; book promptly.' : entry.status}
                        </p>)}
                    </section>
                )}

                {error && (
                    <p className="appointment-error">{error}</p>
                )}

                {successMessage && (
                    <p className="cancellation-success">
                        {successMessage}
                    </p>
                )}

                {cancelError && (
                    <p className="cancellation-error">
                        {cancelError}
                    </p>
                )}

                {!error && appointments.length === 0 && (
                    <p className="no-appointments">
                        You have no appointments yet.
                    </p>
                )}

                <div className="appointment-list">

                    {appointments.map((appointment) => (
                        <div
                            className="appointment-card"
                            key={appointment.id}
                        >

                            <div className="appointment-card-header">

                                <h3>{appointment.doctorName}</h3>

                                <span
                                    className={`appointment-status ${
                                        appointment.status === 'CANCELLED'
                                            ? 'cancelled-status'
                                            : 'confirmed-status'
                                    }`}
                                >
                                    {appointment.status}
                                </span>

                            </div>

                            <p>
                                <strong>Appointment ID:</strong>{' '}
                                {appointment.id}
                            </p>

                            <p>
                                <strong>Patient:</strong>{' '}
                                {appointment.patientName}
                            </p>

                            <p>
                                <strong>Date:</strong>{' '}
                                {appointment.appointmentDate}
                            </p>

                            <p>
                                <strong>Time:</strong>{' '}
                                {appointment.appointmentTime}
                            </p>
                            {queueStatuses[appointment.id] && <p className="queue-estimate">Today's queue: position {queueStatuses[appointment.id].position} · {queueStatuses[appointment.id].patientsAhead} ahead · estimated wait {queueStatuses[appointment.id].estimatedWaitMinutes} min (estimate only)</p>}

                            <button type="button" onClick={() => downloadConfirmation(appointment.id)}>Download confirmation PDF</button>
                            {appointment.status === 'COMPLETED' && !reviewedIds.includes(appointment.id) && <div className="appointment-card">
                                <label>Rate your visit <select value={reviewDrafts[appointment.id]?.rating || 5} onChange={event => setReviewDrafts({ ...reviewDrafts, [appointment.id]: { ...reviewDrafts[appointment.id], rating: event.target.value } })}><option value="5">5 — Excellent</option><option value="4">4 — Good</option><option value="3">3 — Okay</option><option value="2">2 — Poor</option><option value="1">1 — Very poor</option></select></label>
                                <label>Feedback <textarea maxLength="2000" value={reviewDrafts[appointment.id]?.comment || ''} onChange={event => setReviewDrafts({ ...reviewDrafts, [appointment.id]: { ...reviewDrafts[appointment.id], comment: event.target.value } })} /></label>
                                <button type="button" onClick={() => submitReview(appointment.id)}>Submit review</button>
                            </div>}

                            {appointment.status === 'CONFIRMED' && (
                                <>
                                    <button
                                        className="cancel-appointment-btn"
                                        onClick={() => handleCancelAppointment(appointment.id)}
                                        disabled={cancellingId !== null}
                                    >
                                        {cancellingId === appointment.id ? 'Cancelling...' : 'Cancel Appointment'}
                                    </button>
                                    <button type="button" onClick={() => { setReschedulingId(appointment.id); setRescheduleDate(appointment.appointmentDate); setRescheduleTime(appointment.appointmentTime); }}>
                                        Reschedule
                                    </button>
                                    {reschedulingId === appointment.id && (
                                        <form onSubmit={event => { event.preventDefault(); handleReschedule(appointment.id); }}>
                                            <label>New date <input type="date" min={new Date().toLocaleDateString('en-CA')} value={rescheduleDate} onChange={event => setRescheduleDate(event.target.value)} required /></label>
                                            <label>New time <select value={rescheduleTime} onChange={event => setRescheduleTime(event.target.value)} required>
                                                <option value="">Choose time</option>
                                                {(doctors.find(doctor => doctor.id === appointment.doctorId)?.availableSlots || []).map(slot => <option key={slot} value={slot}>{slot}</option>)}
                                            </select></label>
                                            <button type="submit">Confirm reschedule</button>
                                            <button type="button" onClick={() => setReschedulingId(null)}>Keep current time</button>
                                        </form>
                                    )}
                                </>
                            )}
                            {appointment.status === 'OFFERED' && <div role="status"><p>A released slot is reserved for you. Accept it to confirm the appointment.</p><button type="button" onClick={async()=>{const response=await fetch(`http://localhost:8081/api/appointments/${appointment.id}/accept-offer`,{method:'PATCH',headers:{Authorization:`Bearer ${localStorage.getItem('medislot_token')||''}`}});if(response.ok){setAppointments(previous=>previous.map(item=>item.id===appointment.id?{...item,status:'CONFIRMED'}:item));setSuccessMessage('Waiting-list offer accepted.');}}}>Accept slot</button><button type="button" onClick={async()=>{const response=await fetch(`http://localhost:8081/api/appointments/${appointment.id}/decline-offer`,{method:'PATCH',headers:{Authorization:`Bearer ${localStorage.getItem('medislot_token')||''}`}});if(response.ok){setAppointments(previous=>previous.filter(item=>item.id!==appointment.id));setSuccessMessage('Waiting-list offer declined.');}}}>Decline</button></div>}

                        </div>
                    ))}

                </div>

            </div>
        </div>
    );
}

export default PatientAppointments;
