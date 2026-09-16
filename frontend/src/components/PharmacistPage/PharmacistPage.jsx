import { useState } from 'react'
import { Plus, Edit2, Trash2, X, Package, DollarSign, Layers, FileText } from 'lucide-react'

function PharmacistPage({ token, products, setProducts }) {
    const [isFormOpen, setIsFormOpen] = useState(false)
    const [editingProductId, setEditingProductId] = useState(null)
    const [name, setName] = useState('')
    const [price, setPrice] = useState('')
    const [stock, setStock] = useState('')
    const [instruction, setInstruction] = useState('')
    const [fieldErrors, setFieldErrors] = useState({})
    const [generalError, setGeneralError] = useState('')

    const openAddModal = () => {
        setEditingProductId(null)
        setName('')
        setPrice('')
        setStock('')
        setInstruction('')
        setFieldErrors({})
        setGeneralError('')
        setIsFormOpen(true)
    }

    const openEditModal = (product) => {
        setEditingProductId(product.id)
        setName(product.name)
        setPrice(product.price)
        setStock(product.stock || 0)
        setInstruction(product.instruction || '')
        setFieldErrors({})
        setGeneralError('')
        setIsFormOpen(true)
    }

    const handleDeleteProduct = async (id) => {
        if (!window.confirm('Вы уверены, что хотите удалить этот товар из базы данных?')) return

        try {
            const response = await fetch(`http://localhost:8080/api/product/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if (response.ok) {
                setProducts(prev => prev.filter(p => p.id !== id))
            } else {
                alert('Не удалось удалить товар. Проверьте права доступа.')
            }
        } catch (error) {
            console.error('Ошибка при удалении:', error)
        }
    }

        const handleFormSubmit = async (event) => {
            event.preventDefault()
            setFieldErrors({})
            setGeneralError('')

            const isEdit = editingProductId !== null
            const url = isEdit
                ? `http://localhost:8080/api/product/${editingProductId}`
                : 'http://localhost:8080/api/product'
            const method = isEdit ? 'PUT' : 'POST'
            const currentCategory = products[0]?.category || 'MEDICINE'

            const payload = {
                name,
                price: parseFloat(price),
                stock: parseInt(stock),
                category: currentCategory,
                instruction
            }

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(payload)
                })

                const data = await response.json().catch(() => null)

                if (response.ok && data) {
                    if (isEdit) {
                        setProducts(prevProducts =>
                            prevProducts.map(p => p.id === editingProductId ? data : p)
                        )
                    } else {
                        setProducts(prevProducts => [...prevProducts, data])
                    }

                    setIsFormOpen(false)
                } else {
                    if (response.status === 400 && data?.fieldErrors) {
                        setFieldErrors(data.fieldErrors)
                    } else {
                        setGeneralError(data?.message || 'Произошла ошибка при сохранении товара.')
                    }
                }
            } catch (error) {
                console.error('Ошибка отправки формы:', error)
                setGeneralError('Ошибка соединения с сервером.')
            }
        }


        return (
            <div className="pharmacist-page">
                <div className="pharmacist-header">
                    <h2>Панель управления запасами</h2>
                </div>

                {products.length === 0 ? (
                    <div className="pharmacist-empty-state">
                        <Layers size={48} strokeWidth={1} />
                        <p>Выберите категорию в меню «Каталог» выше, чтобы отобразить список медикаментов или добавить новый.</p>
                    </div>
                ) : (
                    <div className="table-responsive">
                        <div style={{ padding: '16px 24px', display: 'flex', justifyContent: 'flex-end', borderBottom: '1px solid var(--border)' }}>
                            <button className="add-product-btn" onClick={openAddModal}>
                                <Plus size={18} />
                                <span>Добавить товар в эту категорию</span>
                            </button>
                        </div>

                        <table className="pharmacist-table">
                            <thead>
                                <tr>
                                    <th>Название лекарства</th>
                                    <th>Цена</th>
                                    <th>Остаток на складе</th>
                                    <th style={{ textAlign: 'right' }}>Действия</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product) => (
                                    <tr key={product.id}>
                                        <td className="prod-name-cell">{product.name}</td>
                                        <td>{product.price} ₸</td>
                                        <td>
                                            <span className={`stock-indicator ${product.stock > 0 ? 'good' : 'empty'}`}>
                                                {product.stock !== undefined && product.stock !== null ? `${product.stock} шт` : '—'}
                                            </span>
                                        </td>
                                        <td style={{ textAlign: 'right' }}>
                                            <div className="table-actions-wrapper">
                                                <button className="action-inline-btn edit" onClick={() => openEditModal(product)}>
                                                    <Edit2 size={14} />
                                                    <span>Редактировать</span>
                                                </button>
                                                <button className="action-inline-btn delete" onClick={() => handleDeleteProduct(product.id)}>
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
                    <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '500px' }}>
                        <button className="modal-close-btn" onClick={() => setIsFormOpen(false)}>
                            <X size={20} />
                        </button>

                        <h3 className="modal-crud-title">
                            {editingProductId !== null ? 'Редактирование товара' : 'Добавление нового товара'}
                        </h3>

                        {generalError && <div className="auth-error-alert" style={{ marginBottom: '16px' }}>{generalError}</div>}

                        <form className="auth-form" onSubmit={handleFormSubmit} noValidate>
                            <div className="input-group">
                                <label>Название товара</label>
                                <div className="input-wrapper">
                                    <Package className="input-icon" size={18} />
                                    <input
                                        type="text"
                                        placeholder="Например: Парацетамол 500мг"
                                        className={fieldErrors.name ? 'input-error' : ''}
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                        required
                                    />
                                </div>
                                {fieldErrors.name && <span className="field-error-text">{fieldErrors.name}</span>}
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="input-group">
                                    <label>Цена (₸)</label>
                                    <div className="input-wrapper">
                                        <DollarSign className="input-icon" size={18} />
                                        <input
                                            type="number"
                                            placeholder="1200"
                                            className={fieldErrors.price ? 'input-error' : ''}
                                            value={price}
                                            onChange={(e) => setPrice(e.target.value)}
                                            required
                                        />
                                    </div>
                                    {fieldErrors.price && <span className="field-error-text">{fieldErrors.price}</span>}
                                </div>

                                <div className="input-group">
                                    <label>Количество на складе</label>
                                    <div className="input-wrapper">
                                        <Layers className="input-icon" size={18} />
                                        <input
                                            type="number"
                                            placeholder="50"
                                            className={fieldErrors.stock ? 'input-error' : ''}
                                            value={stock}
                                            onChange={(e) => setStock(e.target.value)}
                                            required
                                        />
                                    </div>
                                    {fieldErrors.stock && <span className="field-error-text">{fieldErrors.stock}</span>}
                                </div>
                            </div>

                            <div className="input-group">
                                <label>Инструкция по применению</label>
                                <div className="input-wrapper" style={{ alignItems: 'flex-start' }}>
                                    <FileText className="input-icon" size={18} style={{ marginTop: '14px' }} />
                                    <textarea
                                        placeholder="Применять внутрь после еды..."
                                        className={`crud-textarea ${fieldErrors.instruction ? 'input-error' : ''}`}
                                        value={instruction}
                                        onChange={(e) => setInstruction(e.target.value)}
                                        required
                                    />
                                </div>
                                {fieldErrors.instruction && <span className="field-error-text">{fieldErrors.instruction}</span>}
                            </div>

                            <button type="submit" className="auth-submit-btn" style={{ marginTop: '10px' }}>
                                {editingProductId !== null ? 'Сохранить изменения' : 'Добавить лекарство'}
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    )
}

export default PharmacistPage
