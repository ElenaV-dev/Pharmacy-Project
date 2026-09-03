import { ShoppingCart, SearchX } from 'lucide-react'

function SearchResults({ products, onSelectProduct, hasSearched, onAddToCart }) {

    if (hasSearched && products.length === 0) {
        return (
            <div className="no-results">
                <SearchX size={48} strokeWidth={1.5} />
                <h3>Ничего не найдено</h3>
                <p>Попробуйте проверить правильность написания или ввести другое название лекарства.</p>
            </div>
        )
    }

    if (!hasSearched && products.length === 0) {
        return (
            <div className="start-screen">
                <h2>Добро пожаловать в Pharmacy</h2>
                <p>Воспользуйтесь поиском выше, чтобы найти необходимые медикаменты.</p>
            </div>
        )
    }

    return (
        <div className="search-results">
            {products.map((product) => (
                <div className="product-card" key={product.id}>
                      <button
                          className="product-name"
                          onClick={() => onSelectProduct(product.id)}
                      >
                          {product.name}
                      </button>

                       <p className="product-price">
                           {product.price} ₸
                       </p>

                       <p className={`product-stock ${product.inStock ? 'in' : 'out'}`}>
                           {product.inStock ? '✓ Есть в наличии' : 'Нет в наличии'}
                       </p>

                       <button
                           className="cart-button"
                           onClick={() => onAddToCart(product)}
                           disabled={!product.inStock}
                       >
                           <ShoppingCart size={16} />
                           <span>Купить</span>
                       </button>
                </div>
            ))}
        </div>
    )
}

export default SearchResults
