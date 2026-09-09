import { useState } from 'react'
import { X, Lock, Mail, User, Eye, EyeOff } from 'lucide-react'

function AuthModal({ onClose, onAuthSuccess }) {
    const [isLoginMode, setIsLoginMode] = useState(true)
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [fieldErrors, setFieldErrors] = useState({})
    const [generalError, setGeneralError] = useState('')
    const [successMessage, setSuccessMessage] = useState('')
    const [loading, setLoading] = useState(false)
    const [showPassword, setShowPassword] = useState(false)
    const [loginEmail, setLoginEmail] = useState('')
    const [loginPassword, setLoginPassword] = useState('')

    const handleLoginSubmit = async (event) => {
            event.preventDefault()
            setFieldErrors({})
            setGeneralError('')
            setLoading(true)

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: loginEmail, password: loginPassword })
                })

                const data = await response.json().catch(() => null)
                if (response.ok && data?.token) {
                    onAuthSuccess(data.token)
                } else {
                    if (response.status === 400 && data?.fieldErrors) {
                        setFieldErrors(data.fieldErrors)
                    } else {
                        setGeneralError(data?.message || 'Неверный адрес почты или пароль.')
                    }
                }
            } catch (err) {
                console.error(err)
                setGeneralError('Ошибка соединения с сервером.')
            } finally {
                setLoading(false)
            }
        }

    const handleRegisterSubmit = async (event) => {
            event.preventDefault()
            setFieldErrors({})
            setGeneralError('')
            setLoading(true)

            try {
                const response = await fetch('http://localhost:8080/api/auth/registration', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password })
                })

                const data = await response.json().catch(() => null)

            if (response.ok && data?.token) {
                onAuthSuccess(data.token)
            } else {
                if (data && data.fieldErrors) {
                    setFieldErrors(data.fieldErrors)
                } else {
                    setGeneralError(data?.message || 'Ошибка регистрации.')
                }
            }
        } catch (err) {
            console.error(err)
            setGeneralError('Ошибка соединения.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>

                <button className="modal-close-btn" onClick={onClose}>
                    <X size={20} />
                </button>

                <div className="modal-tabs">
                    <button
                        className={`modal-tab ${isLoginMode ? 'active' : ''}`}
                        onClick={() => { setIsLoginMode(true); setFieldErrors({}); setGeneralError(''); setSuccessMessage(''); }}
                    >
                        Вход
                    </button>
                    <button
                        className={`modal-tab ${!isLoginMode ? 'active' : ''}`}
                        onClick={() => { setIsLoginMode(false); setFieldErrors({}); setGeneralError(''); setSuccessMessage(''); }}
                    >
                        Регистрация
                    </button>
                </div>

                {generalError && <div className="auth-error-alert">{generalError}</div>}
                {successMessage && <div className="auth-success-alert">{successMessage}</div>}

                {isLoginMode ? (
                    <form className="auth-form" onSubmit={handleLoginSubmit} noValidate>
                        <div className="input-group">
                            <label>Электронная почта</label>
                            <div className="input-wrapper">
                                <Mail className="input-icon" size={18} />
                                <input
                                    type="email"
                                    placeholder="example@mail.com"
                                    className={fieldErrors.email ? 'input-error' : ''}
                                    value={loginEmail}
                                    onChange={(e) => setLoginEmail(e.target.value)}
                                    required
                                />
                            </div>
                            {fieldErrors.email && <span className="field-error-text">{fieldErrors.email}</span>}
                        </div>

                        <div className="input-group">
                            <label>Пароль</label>
                            <div className="input-wrapper">
                                <Lock className="input-icon" size={18} />
                                <input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="••••••••"
                                    className={fieldErrors.password ? 'input-error' : ''}
                                    value={loginPassword}
                                    onChange={(e) => setLoginPassword(e.target.value)}
                                    required
                                />
                                <button
                                    type="button"
                                    className="password-toggle-btn"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            {fieldErrors.password && <span className="field-error-text">{fieldErrors.password}</span>}
                        </div>

                        {generalError && <div className="auth-error-alert">{generalError}</div>}

                        <button type="submit" className="auth-submit-btn" disabled={loading}>
                            {loading ? 'Вход в аккаунт...' : 'Войти'}
                        </button>
                    </form>
                ) : (
                    <form className="auth-form" onSubmit={handleRegisterSubmit} noValidate>

                        <div className="input-group">
                            <label>Ваше имя</label>
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
                                    placeholder="example@mail.com"
                                    className={fieldErrors.email ? 'input-error' : ''}
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            {fieldErrors.email && <span className="field-error-text">{fieldErrors.email}</span>}
                        </div>

                        <div className="input-group">
                            <label>Пароль</label>
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
                                <button
                                    type="button"
                                    className="password-toggle-btn"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            {fieldErrors.password ? (
                                <span className="field-error-text">{fieldErrors.password}</span>
                            ) : (
                                <span className="input-hint">Минимум 8 символов: заглавная, строчная буква и цифра</span>
                            )}
                        </div>

                        <button type="submit" className="auth-submit-btn" disabled={loading}>
                            {loading ? 'Создание аккаунта...' : 'Создать аккаунт'}
                        </button>
                    </form>
                )}
            </div>
        </div>
    )
}

export default AuthModal
