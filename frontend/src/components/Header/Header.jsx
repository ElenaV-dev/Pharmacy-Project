import SearchBar from '../SearchBar/SearchBar'

function Header() {
    return (
        <header>
            <h1>Pharmacy</h1>

            <nav>
                <button>Каталог</button>
            </nav>

            <SearchBar />

            <div className="user-actions">
                <button>Войти</button>
                <button>Корзина</button>
            </div>
        </header>
    )
}

export default Header