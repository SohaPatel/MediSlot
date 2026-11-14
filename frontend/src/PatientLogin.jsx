
import { useState } from 'react';
import './PatientRegistration.css';

function PatientLogin({ onLogin, onSignup }) {

    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

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

        setError('');
        setLoading(true);

        try {
            const response = await fetch(
                'http://localhost:8081/api/patients/login',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message || 'Invalid email or password'
                );
            }

            onLogin(data);

        } catch (err) {
            setError(err.message || 'Unable to connect to server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="registration-container">
            <div className="registration-card">

                <h2>Welcome Back!</h2>

                <p>Login to your MediSlot account.</p>

                <form onSubmit={handleSubmit}>

                    <input
                        type="email"
                        name="email"
                        placeholder="Email Address"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />

                    <button type="submit" disabled={loading}>
                        {loading ? 'Logging in...' : 'Login'}
                    </button>

                </form>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                <p>
                    Don't have an account?{' '}
                    <button
                        type="button"
                        onClick={onSignup}
                        className="login-link"
                    >
                        Sign Up
                    </button>
                </p>

            </div>
        </div>
    );
}

export default PatientLogin;
