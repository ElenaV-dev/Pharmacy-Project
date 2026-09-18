import { useState, useEffect } from 'react'
import { Plus, Edit2, Trash2, X, User, Mail, Lock, ShieldAlert, Eye, EyeOff } from 'lucide-react'


function AdminPage({ token }) {
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(true)
    const [isFormOpen, setIsFormOpen] = useState(false)
    const [editingUserId, setEditingUserId] = useState(null)
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [role, setRole] = useState('CLIENT')
    const [fieldErrors, setFieldErrors] = useState({})
    const [generalError, setGeneralError] = useState('')
    const [showPassword, setShowPassword] = useState(false)

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoading(true)
                const response = await fetch('http://localhost:8080/api/user', {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                })
                if (response.ok) {
                    const data = await response.json()
                    setUsers(data)
                } else {
                    setGeneralError('Не удалось загрузить список пользователей.')
                }
            } catch (error) {
                console.error(error)
                setGeneralError('Ошибка соединения с сервером.')
            } finally {
                setLoading(false)
            }
        }
        fetchUsers()
    }, [token])

    const openAddModal = () => {
        setEditingUserId(null)
        setName('')
        setEmail('')
        setPassword('')
        setRole('CLIENT')
        setFieldErrors({})
        setGeneralError('')
        setIsFormOpen(true)
    }

    const openEditModal = (user) => {
        setEditingUserId(user.id)
        setName(user.name)
        setEmail(user.email)
        setPassword('')
        setRole(user.role)
        setFieldErrors({})
        setGeneralError('')
        setIsFormOpen(true)
    }

    const handleDeleteUser = async (id) => {
        if (!window.confirm('Вы уверены, что хотите удалить этого пользователя?')) return

        try {
            const response = await fetch(`http://localhost:8080/api/user/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            })

            if (response.ok) {
                setUsers(prev => prev.filter(u => u.id !== id))
            } else {
                alert('Ошибка при удалении пользователя.')
            }
        } catch (error) {
            console.error(error)
        }
    }

    const handleFormSubmit = async (event) => {
        event.preventDefault()
        setFieldErrors({})
        setGeneralError('')

        const isEdit = editingUserId !== null
        const url = isEdit ? `http://localhost:8080/api/user/${editingUserId}` : 'http://localhost:8080/api/user'
        const method = isEdit ? 'PUT' : 'POST'

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ name, email, password, role })
            })

            const data = await response.json().catch(() => null)

            if (response.ok && data) {
                if (isEdit) {
                    setUsers(prev => prev.map(u => u.id === editingUserId ? data : u))
                } else {
                    setUsers(prev => [...prev, data])
                }
                setIsFormOpen(false)
            } else {
                if (response.status === 400 && data?.fieldErrors) {
                    setFieldErrors(data.fieldErrors)
                } else {
                    setGeneralError(data?.message || 'Ошибка сохранения данных.')
                }
            }
        } catch (error) {
            console.error(error)
            setGeneralError('Ошибка соединения.')
        }
    }

    if (loading) return <div className="loading">Загрузка панели администратора...</div>

    return (
        <div className="pharmacist-page">
            <div className="pharmacist-header">
                <h2>Управление пользователями</h2>
                <button className="add-product-btn" onClick={openAddModal}>
                    <Plus size={18} />
                    <span>Добавить пользователя</span>
                </button>
            </div>

            {generalError && <div className="auth-error-alert" style={{ marginBottom: '16px' }}>{generalError}</div>}

            {users.length === 0 ? (
                <div className="pharmacist-empty-state">
                    <User size={48} strokeWidth={1} />
                    <p>Пользователи не найдены в базе данных.</p>
                </div>
            ) : (
                <div className="table-responsive">
                    <table className="pharmacist-table">
                        <thead>
                            <tr>
                                <th>Имя пользователя</th>
                                <th>Email (Логин)</th>
                                <th>Роль в системе</th>
                                <th style={{ textAlign: 'right' }}>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((user) => (
                                <tr key={user.id}>
                                    <td className="prod-name-cell">{user.name}</td>
                                    <td>{user.email}</td>
                                    <td>
                                        <span className={`stock-indicator ${user.role === 'ADMIN' ? 'empty' : 'good'}`}>
                                            {user.role}
                                        </span>
                                    </td>
                                    <td style={{ textAlign: 'right' }}>
                                        <div className="table-actions-wrapper">
                                            <button className="action-inline-btn edit" onClick={() => openEditModal(user)}>
                                                <Edit2 size={14} />
                                                <span>Редактировать</span>
                                            </button>
                                            <button className="action-inline-btn delete" onClick={() => handleDeleteUser(user.id)}>
                                                <Trash2 size={14} />
                                                <span>Удалить</span>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {isFormOpen && (
                <div className="modal-overlay" onClick={() => setIsFormOpen(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '460px' }}>
                        <button className="modal-close-btn" onClick={() => setIsFormOpen(false)}>
                            <X size={20} />
                        </button>

                        <h3 className="modal-crud-title">
                            {editingUserId !== null ? 'Редактировать профиль' : 'Создать пользователя'}
                        </h3>

                        <form className="auth-form" onSubmit={handleFormSubmit} noValidate>
                            <div className="input-group">
                                <label>Имя сотрудника / клиента</label>
                                <div className="input-wrapper">
                                    <User className="input-icon" size={18} />
                                    <input
                                        type="text"
                                        placeholder="Иван"
                                        className={fieldErrors.name ? 'input-error' : ''}
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                        required
                                    />
                                </div>
                                {fieldErrors.name && <span className="field-error-text">{fieldErrors.name}</span>}
                            </div>

                            <div className="input-group">
                                <label>Электронная почта</label>
                                <div className="input-wrapper">
                                    <Mail className="input-icon" size={18} />
                                    <input
                                        type="email"
                                        placeholder="user@pharmacy.com"
                                        className={fieldErrors.email ? 'input-error' : ''}
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    />
                                </div>
                                {fieldErrors.email && <span className="field-error-text">{fieldErrors.email}</span>}
                            </div>

                            <div className="input-group">
                                <label>Пароль аккаунта</label>
                                <div className="input-wrapper">
                                    <Lock className="input-icon" size={18} />
                                        <input
                                            type={showPassword ? "text" : "password"}
                                            placeholder="••••••••"
                                            className={fieldErrors.password ? 'input-error' : ''}
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                        />
                                            <button type="button" className="password-toggle-btn" onClick={() => setShowPassword(!showPassword)}>
                                                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                            </button>
                                        </div>
                                        {fieldErrors.password && <span className="field-error-text">{fieldErrors.password}</span>}
                                    </div>

                            <div className="input-group">
                                <label>Назначить роль доступа</label>
                                <div className="input-wrapper">
                                    <ShieldAlert className="input-icon" size={18} style={{ zIndex: 10 }} />
                                    <select
                                        className={`crud-textarea ${fieldErrors.role ? 'input-error' : ''}`}
                                        style={{ minHeight: 'auto', padding: '12px 14px 12px 42px', appearance: 'auto' }}
                                        value={role}
                                        onChange={(e) => setRole(e.target.value)}
                                    >
                                        <option value="CLIENT">CLIENT (Покупатель)</option>
                                        <option value="PHARMACIST">PHARMACIST (Фармацевт)</option>
                                        <option value="ADMIN">ADMIN (Администратор)</option>
                                        </select>
                                    </div>
                                    {fieldErrors.role && <span className="field-error-text">{fieldErrors.role}</span>}
                                </div>

                                <button type="submit" className="auth-submit-btn" style={{ marginTop: '12px' }}>
                                    {editingUserId !== null ? 'Сохранить профиль' : 'Зарегистрировать'}
                                </button>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        )
    }

export default AdminPage
