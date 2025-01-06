import React from 'react';
import { Link } from 'react-router-dom';
import UserService from '../../services/UserService';
import { useStateContext } from '../../contexts/ContextProvider';

function Navbar() {
    const { user, token, setToken } = useStateContext();

    const handleLogout = () => {
        const confirmDelete = window.confirm('Are you sure you want to logout this user?');
        if (confirmDelete) {
            UserService.logout();
            setToken("");
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">Control</div>
            <ul className="navbar-links">
                {!token && <li><Link to="/login">Login</Link></li>}
                {!token && <li><Link to="/register">Register</Link></li>}
                {token && <li><Link to="/dashboard">Dashboard</Link></li>}
                {token && (
                    <li>
                        <button onClick={handleLogout} className="logout-button">
                            Logout: {user.name}
                        </button>
                    </li>
                )}
            </ul>
        </nav>
    );
}

export default Navbar;
