import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    nombreCompleto: '',
    telefono: '',
    direccion: '',
    marcaPreferida: 'Apple',
    sistemaOperativoPreferido: 'iOS',
    rangoPrecioMin: 1000,
    rangoPrecioMax: 5000,
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const value = e.target.type === 'number' ? parseFloat(e.target.value) : e.target.value;
    setFormData({ ...formData, [e.target.name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await register(formData);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrarse');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4 py-12 bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="w-full max-w-2xl">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold gradient-text mb-2">Crear Cuenta</h1>
          <p className="text-slate-600">Únete a Tech Boutique</p>
        </div>

        <div className="bg-white rounded-2xl shadow-xl p-8">
          {error && (
            <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Usuario</label>
              <input type="text" name="username" value={formData.username} onChange={handleChange} required className="input-field" />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Email</label>
              <input type="email" name="email" value={formData.email} onChange={handleChange} required className="input-field" />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-semibold text-slate-700 mb-2">Nombre Completo</label>
              <input type="text" name="nombreCompleto" value={formData.nombreCompleto} onChange={handleChange} required className="input-field" />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Contraseña</label>
              <input type="password" name="password" value={formData.password} onChange={handleChange} required className="input-field" />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Teléfono</label>
              <input type="tel" name="telefono" value={formData.telefono} onChange={handleChange} className="input-field" />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-semibold text-slate-700 mb-2">Dirección</label>
              <input type="text" name="direccion" value={formData.direccion} onChange={handleChange} className="input-field" />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Marca Preferida</label>
              <select name="marcaPreferida" value={formData.marcaPreferida} onChange={handleChange} className="input-field">
                <option value="Apple">Apple</option>
                <option value="Samsung">Samsung</option>
                <option value="Google">Google</option>
                <option value="Dell">Dell</option>
                <option value="Sony">Sony</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Sistema Operativo</label>
              <select name="sistemaOperativoPreferido" value={formData.sistemaOperativoPreferido} onChange={handleChange} className="input-field">
                <option value="iOS">iOS</option>
                <option value="Android">Android</option>
                <option value="Windows">Windows</option>
                <option value="MacOS">MacOS</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Precio Mínimo (S/.)</label>
              <input type="number" name="rangoPrecioMin" value={formData.rangoPrecioMin} onChange={handleChange} className="input-field" />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Precio Máximo (S/.)</label>
              <input type="number" name="rangoPrecioMax" value={formData.rangoPrecioMax} onChange={handleChange} className="input-field" />
            </div>

            <div className="md:col-span-2">
              <button type="submit" disabled={loading} className="w-full btn-primary">
                {loading ? 'Registrando...' : 'Crear Cuenta'}
              </button>
            </div>
          </form>
        </div>

        <p className="text-center text-slate-600 mt-6">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="font-semibold text-purple-600 hover:text-purple-700">
            Inicia sesión
          </Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;