import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import UserService from "../../services/UserService";
import { useStateContext } from "../../contexts/ContextProvider";
import { Link } from 'react-router-dom';


function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { setUser, setToken } = useStateContext();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const userData = await UserService.login(email, password);
            if (userData.token) {
                setToken(userData.token);
                setUser({ ...userData });
                navigate("/dashboard");
            } else {
                setError(userData.message);
            }
        } catch (error) {
            setError(error.message);
            setTimeout(() => setError(""), 5000);
        }
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-container">
                <div className="auth-title">
                    <h2>Login</h2>
                    <Link to="/register">Register</Link>
                </div>
                {error && <p className="error-message">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Email:</label>
                        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label>Password:</label>
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                    </div>
                    <button type="submit">Login</button>
                </form>
            </div>
        </div>

    );
}

export default LoginPage;