import { useState, useEffect } from 'react'
import { Plus, Edit2, Trash2, X, User, Mail, Lock, ShieldAlert, Eye, EyeOff, ClipboardList, CheckCircle2, XCircle, Calendar, CreditCard } from 'lucide-react'

function AdminPage({ token }) {
    const [activeTab, setActiveTab] = useState('users')
    const [users, setUsers] = useState([])
    const [loadingUsers, setLoadingUsers] = useState(true)
    const [isFormOpen, setIsFormOpen] = useState(false)
    const [editingUserId, setEditingUserId] = useState(null)
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [role, setRole] = useState('CLIENT')
    const [fieldErrors, setFieldErrors] = useState({})
    const [generalError, setGeneralError] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [orders, setOrders] = useState([])
    const [loadingOrders, setLoadingOrders] = useState(false)

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoadingUsers(true)
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
                setLoadingUsers(false)
            }
        }
        fetchUsers()
    }, [token])

        useEffect(() => {
            if (!token || activeTab !== 'orders') return

            const fetchAllOrders = async () => {
                setLoadingOrders(true)
                try {
                    const response = await fetch('http://localhost:8080/api/orders/admin', {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    })

                    if (response.ok) {
                        const data = await response.json()

                        const sortedOrders = (data || []).sort((a, b) => {
                            if (a.status === 'PROCESSING' && b.status !== 'PROCESSING') return -1;
                            if (a.status !== 'PROCESSING' && b.status === 'PROCESSING') return 1;

                            return new Date(b.orderDate) - new Date(a.orderDate);
                        })

                        setOrders(sortedOrders)
                    } else {
                        console.error("Не удалось загрузить все заказы администратором")
                    }
                } catch (error) {
                    console.error("Ошибка при получении всех заказов:", error)
                } finally {
                    setLoadingOrders(false)
                }
            }

            fetchAllOrders()
        }, [token, activeTab])

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

    const handleUpdateStatus = async (orderId, newStatus) => {
        try {
            const response = await fetch(`http://localhost:8080/api/orders/${orderId}/status`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status: newStatus })
            })

            if (response.ok) {
                const updatedOrder = await response.json()
                setOrders(prevOrders => prevOrders.map(order =>
                    order.id === orderId ? { ...order, status: updatedOrder.status } : order
                ))
            } else {
                alert("Не удалось изменить статус заказа")
            }
        } catch (error) {
            console.error("Ошибка при обновлении статуса:", error)
        }
    }

    const formatDate = (dateString) => {
        if (!dateString) return ''
        const date = new Date(dateString)
        return date.toLocaleDateString('ru-RU', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        })
    }

    return (
        <div className="pharmacist-page">

            <div className="modal-tabs" style={{ marginBottom: '24px', justifyContent: 'flex-start', display: 'flex', gap: '8px' }}>
                <button
                    className={`modal-tab ${activeTab === 'users' ? 'active' : ''}`}
                    onClick={() => setActiveTab('users')}
                >
                    Управление пользователями
                </button>
                <button
                    className={`modal-tab ${activeTab === 'orders' ? 'active' : ''}`}
                    onClick={() => setActiveTab('orders')}
                >
                    Управление заказами ({orders.length})
                </button>
            </div>

            {generalError && <div className="auth-error-alert" style={{ marginBottom: '16px' }}>{generalError}</div>}
            {activeTab === 'users' && (
                loadingUsers ? (
                    <div className="loading" style={{ textAlign: 'center', padding: '20px' }}>Загрузка пользователей...</div>
                ) : users.length === 0 ? (
                    <div className="pharmacist-empty-state">
                        <User size={48} strokeWidth={1} />
                        <p>Пользователи не найдены в базе данных.</p>
                        <button className="add-product-btn" onClick={openAddModal} style={{ marginTop: '12px' }}>
                            <Plus size={18} />
                            <span>Добавить пользователя</span>
                        </button>
                    </div>
                ) : (
                    <>
                    <div className="pharmacist-header" style={{ marginBottom: '16px' }}>
                        <h2>Список пользователей</h2>
                        <button className="add-product-btn" onClick={openAddModal}>
                            <Plus size={18} />
                            <span>Добавить пользователя</span>
                        </button>
                    </div>
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
                    </>
                )
            )}

            {activeTab === 'orders' && (
                loadingOrders ? (
                    <div className="loading" style={{ textAlign: 'center', padding: '20px' }}>Загрузка списка заказов...</div>
                ) : orders.length === 0 ? (
                    <div className="pharmacist-empty-state" style={{ textAlign: 'center', padding: '40px' }}>
                        <ClipboardList size={64} strokeWidth={1} style={{ color: 'var(--text-muted)' }} />
                        <p>Заказов в системе пока нет.</p>
                    </div>
                ) : (
                    <>
                        <div className="pharmacist-header" style={{ marginBottom: '16px' }}>
                            <h2>Все заказы клиентов</h2>
                        </div>
                        <div className="admin-orders-list" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                            {orders.map((order) => (
                                <div className="cart-item-card" key={order.id} style={{ display: 'grid', gridTemplateColumns: '1fr auto', alignItems: 'center', padding: '20px 24px', border: '1px solid var(--border-color, #e5e7eb)', borderRadius: '8px' }}>
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '700', fontSize: '16px' }}>
                                            <ClipboardList size={18} style={{ color: 'var(--primary)' }} />
                                                <span>Заказ № {order.orderNumber || order.id}</span>
                                                <span className={`stock-indicator ${order.status === 'COMPLETED' ? 'good' : order.status === 'CANCELLED' ? 'bad' : 'warning'}`} style={{
                                                    backgroundColor: order.status === 'COMPLETED' ? '#f0fdf4' : order.status === 'CANCELLED' ? '#fef2f2' : '#fffbeb',
                                                    color: order.status === 'COMPLETED' ? '#16a34a' : order.status === 'CANCELLED' ? '#dc2626' : '#d97706',
                                                    fontSize: '12px', padding: '2px 8px', borderRadius: '4px', marginLeft: '8px'
                                                }}>
                                            {order.status}
                                        </span>
                                    </div>
                                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', fontSize: '14px', color: 'var(--text-muted)' }}>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                            <User size={14} /> Клиент: {order.name || 'Не указан'}
                                        </span>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                            <Calendar size={14} /> {formatDate(order.orderDate)}
                                        </span>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                                            <CreditCard size={14} /> Сумма: {order.totalPrice} ₸
                                        </span>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                                    {order.status === 'PROCESSING' && (
                                        <>
                                        <button
                                        className="auth-submit-btn"
                                        style={{ margin: 0, padding: '6px 12px', display: 'flex', alignItems: 'center', gap: '4px', backgroundColor: '#16a34a', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                                        onClick={() => handleUpdateStatus(order.id, 'COMPLETED')}
                                    >
                                    <CheckCircle2 size={14} /> Выполнен
                                </button>
                                <button
                                className="delete-item-btn"
                                style={{ margin: 0, padding: '6px 12px', display: 'flex', alignItems: 'center', gap: '4px', backgroundColor: '#dc2626', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                                onClick={() => handleUpdateStatus(order.id, 'CANCELED')}
                            >
                            <XCircle size={14} /> Отменить
                        </button>
                    </>
                )}
                                    {order.status !== 'PROCESSING' && (
                                        <span style={{ color: 'var(--text-muted)', fontSize: '13px', fontStyle: 'italic' }}>
                                            Архивный заказ
                                        </span>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </>
            )
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

export default AdminPage;

