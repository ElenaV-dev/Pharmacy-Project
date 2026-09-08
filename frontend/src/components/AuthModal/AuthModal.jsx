import { useState } from 'react'
import { X, Lock, Mail, User, Eye, EyeOff } from 'lucide-react'

function AuthModal({ onClose }) {
    const [isLoginMode, setIsLoginMode] = useState(false)

    // Поля формы
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    // Вместо одной строки используем объект для точечных ошибок полей
    const [fieldErrors, setFieldErrors] = useState({})
    // Общая ошибка на случай падения сервера (500)
    const [generalError, setGeneralError] = useState('')
    const [successMessage, setSuccessMessage] = useState('')
    const [loading, setLoading] = useState(false)
    const [showPassword, setShowPassword] = useState(false)

    const handleRegisterSubmit = async (event) => {
        event.preventDefault()
        setFieldErrors({})
        setGeneralError('')
        setSuccessMessage('')
        setLoading(true)

        try {
            const response = await fetch('http://localhost:8080/api/auth/registration', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name, email, password })
            })

            const data = await response.json().catch(() => null)

            if (response.ok) {
                setSuccessMessage('Регистрация прошла успешно! Теперь вы можете войти.')
                setName('')
                setEmail('')
                setPassword('')
            } else {
                if (data && data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
                    // Если есть ошибки полей, записываем ТОЛЬКО их. Общую плашку не трогаем!
                    setFieldErrors(data.fieldErrors)
                } else {
                    // Сюда попадем только если упала критическая ошибка бэкенда (например, базы данных)
                    setGeneralError(data?.message || 'Ошибка регистрации. Проверьте введенные данные.')
                }
            }
        } catch (err) {
            console.error(err)
            setGeneralError('Не удалось связаться с сервером. Попробуйте позже.')
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

                {/* Общая ошибка или сообщение об успехе */}
                {generalError && <div className="auth-error-alert">{generalError}</div>}
                {successMessage && <div className="auth-success-alert">{successMessage}</div>}

                {isLoginMode ? (
                    <form className="auth-form" onSubmit={(e) => e.preventDefault()}>
                        <p style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                            Форма входа будет реализована далее.
                        </p>
                    </form>
                ) : (
                    <form className="auth-form" onSubmit={handleRegisterSubmit} noValidate>

                        {/* ПОЛЕ: ИМЯ */}
                        <div className="input-group">
                            <label>Ваше имя</label>
                            <div className="input-wrapper">
                                <User className="input-icon" size={18} />
                                <input
                                    type="text"
                                    placeholder="Иван"
                                    className={fieldErrors.name ? 'input-error' : ''} // Подсветка рамки инпута красным
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                            </div>
                            {/* Вывод ошибки строго под инпутом имени */}
                            {fieldErrors.name && <span className="field-error-text">{fieldErrors.name}</span>}
                        </div>

                        {/* ПОЛЕ: EMAIL */}
                        <div className="input-group">
                            <label>Электронная почта</label>
                            <div className="input-wrapper">
                                <Mail className="input-icon" size={18} />
                                <input
                                    type="email"
                                    placeholder="example@mail.com"
                                    className={fieldErrors.email ? 'input-error' : ''} // Подсветка рамки инпута красным
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            {/* Вывод ошибки строго под инпутом почты */}
                            {fieldErrors.email && <span className="field-error-text">{fieldErrors.email}</span>}
                        </div>

                        {/* ПОЛЕ: ПАРОЛЬ */}
                        <div className="input-group">
                            <label>Пароль</label>
                            <div className="input-wrapper">
                                <Lock className="input-icon" size={18} />
                                <input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="••••••••"
                                    className={fieldErrors.password ? 'input-error' : ''} // Подсветка рамки инпута красным
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
                            {/* Если ошибки нет — показываем обычную подсказку, если есть — текст ошибки красным */}
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
