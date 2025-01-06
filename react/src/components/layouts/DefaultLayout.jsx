import { React, useEffect } from "react";
import { Navigate, Outlet } from 'react-router-dom';
import { useStateContext } from '../../contexts/ContextProvider';
import UserService from "../../services/UserService";
import Navbar from "../common/Navbar";
import Footer from "../common/Footer";

export default function DefaultLayout() {
  const { token, user, setUser } = useStateContext();

  if (!token) {
    return <Navigate to='/login' />;
  }


  useEffect(() => {
    const fetchData = async () => {
      if (!token) {
        return;
      }
      try {
        const userData = await UserService.getUserData(token);
        setUser({ ...userData });
      } catch (error) {
        // console.error(error);
      } finally {
        // setIsLoading(false);
      }
    };

    fetchData();
  }, [token, setUser]);

  return (
    <div>
      <Navbar/>
      <header>Авторизованный пользователь: {user.name}</header>
      <Outlet />
      <Footer/>
    </div>
  );
}