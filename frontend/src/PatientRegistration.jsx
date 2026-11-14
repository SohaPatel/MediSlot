
import { useState } from 'react';
import './PatientRegistration.css';

function PatientRegistration({ onRegistered, onBackToLogin }) {

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        age: '',
        password: ''
    });

    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        setMessage('');
        setError('');
        setLoading(true);

        try {
            const response = await fetch(
                'http://localhost:8081/api/patients/register',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        ...formData,
                        age: Number(formData.age)
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message || 'Registration failed'
                );
            }

            setMessage('Registration successful! Please login.');

            setFormData({
                name: '',
                email: '',
                phone: '',
                age: '',
                password: ''
            });

            if (onRegistered) {
                onRegistered();
            }

        } catch (err) {
            setError(err.message || 'Unable to connect to server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="registration-container">

            <div className="registration-card">

                <h2>Create Your Account</h2>

                <p>Register with MediSlot to manage your appointments.</p>

                <form onSubmit={handleSubmit}>

                    <input
                        type="text"
                        name="name"
                        placeholder="Full Name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        minLength="2"
                        maxLength="100"
                    />

                    <input
                        type="email"
                        name="email"
                        placeholder="Email Address"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="tel"
                        name="phone"
                        placeholder="10-digit Phone Number"
                        value={formData.phone}
                        onChange={handleChange}
                        pattern="[0-9]{10}"
                        maxLength="10"
                        required
                    />

                    <input
                        type="number"
                        name="age"
                        placeholder="Age"
                        value={formData.age}
                        onChange={handleChange}
                        min="1"
                        max="120"
                        required
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder="Create Password (minimum 8 characters)"
                        value={formData.password}
                        onChange={handleChange}
                        minLength="8"
                        maxLength="72"
                        required
                    />

                    <button type="submit" disabled={loading}>
                        {loading ? 'Registering...' : 'Create Account'}
                    </button>

                </form>

                {message && (
                    <div className="success-message">
                        {message}
                    </div>
                )}

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                <p>
                    Already have an account?{' '}
                    <button
                        type="button"
                        onClick={onBackToLogin}
                        className="login-link"
                    >
                        Login
                    </button>
                </p>

            </div>

        </div>
    );
}

export default PatientRegistration;
