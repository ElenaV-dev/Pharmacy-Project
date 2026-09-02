import { useState, useEffect } from 'react'
import { Pill, ShieldCheck, FileText } from 'lucide-react'

function ProductPage({ id }) {
    const [product, setProduct] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchProductData = async () => {
            try {
                setLoading(true)

                const response = await fetch(`http://localhost:8080/api/product/${id}`)
                const data = await response.json()

                setProduct(data)
            } catch (error) {
                console.error("Ошибка при загрузке товара:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchProductData()
    }, [id])

    if (loading) {
        return <div className="loading">Загрузка информации о лекарстве...</div>
    }

    if (!product) {
        return <div className="error">Товар не найден</div>
    }

    return (
        <div className="product-page-container">
            <div className="product-page-media">
                <div className="product-page-image-placeholder">
                    <Pill size={64} strokeWidth={1} />
                </div>
            </div>

            <div className="product-page-info">
                <h2 className="product-page-title">{product.name}</h2>

                <div className="product-page-meta">
                    <p className="product-page-price">{product.price} ₸</p>

                    <span className={`stock-badge ${product.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                        <ShieldCheck size={16} />
                        {product.stock > 0 ? `В наличии: ${product.stock} шт.` : 'Нет в наличии'}
                    </span>
                </div>

                <div className="product-page-instructions">
                    <h3>
                        <FileText size={18} />
                        <span>Инструкция по применению</span>
                    </h3>
                    <div className="instructions-text">
                        {product.instruction ? product.instruction : "Инструкция временно отсутствует."}
                    </div>
                </div>

                <button className="product-page-buy-btn" disabled={product.stock === 0}>
                    {product.stock > 0 ? 'Добавить в корзину' : 'Заказать'}
                </button>
            </div>
        </div>
    )
}

export default ProductPage
