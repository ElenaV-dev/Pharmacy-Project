import { useState } from 'react'
import { Search } from 'lucide-react'

function SearchBar({ setProducts }) {
    const [searchText, setSearchText] = useState('')

    const handleSearch = async (event) => {
        event.preventDefault()

        const response = await fetch(
            `http://localhost:8080/api/product/search?name=${searchText}`
        )

        const data = await response.json()

        setProducts(data)
    }

    return (
        <div className="search-container">
            <form onSubmit={handleSearch}>
                <input
                    className="search-bar"
                    type="text"
                    placeholder="Поиск товаров"
                    value={searchText}
                    onChange={(event) => setSearchText(event.target.value)}
                />

                <button
                    className="search-button"
                    type="submit"
                >
                 <Search size={20} />
                </button>
            </form>
        </div>
    )
}

export default SearchBar