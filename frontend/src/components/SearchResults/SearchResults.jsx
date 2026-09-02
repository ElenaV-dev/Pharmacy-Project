import { ShoppingCart, Pill } from 'lucide-react'

function SearchResults({ products }) {
    return (
        <div className="search-results">
            {products.map((product) => (
                <div className="product-card" key={product.id}>
                    {/* Визуальная заглушка под фото лекарства */}
                    <div className="product-image-placeholder">
                        <Pill size={32} strokeWidth={1.5} />
                    </div>

                    <button className="product-name">
                        {product.name}
                    </button>

                    <p className={`product-stock ${product.inStock ? 'in' : 'out'}`}>
                        {product.inStock ? '✓ В наличии' : 'Нет в наличии'}
                    </p>

                    <div className="product-footer">
                        <p className="product-price">
                            {product.price} ₸
                        </p>
                        <button className="cart-button">
                            <ShoppingCart size={16} />
                            <span>Купить</span>
                        </button>
                    </div>
                </div>
            ))}
        </div>
    )
}

export default SearchResults
