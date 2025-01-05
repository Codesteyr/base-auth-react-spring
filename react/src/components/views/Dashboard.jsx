import React from 'react';
import { useStateContext } from '../../contexts/ContextProvider';

export default function Dashboard() {
  const { user } = useStateContext();


  const handleViewUserData = (e) => {
    e.preventDefault();

    console.log(user);
  }

  return (
    <div>
      Dashboard
      <button onClick={handleViewUserData}>View user</button>
    </div>
  );
}