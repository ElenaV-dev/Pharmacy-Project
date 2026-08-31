import { useState } from 'react'

function SearchBar() {
    const [searchText, setSearchText] = useState('')

    const handleSearch = (event) => {
        event.preventDefault()
        console.log(searchText)
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
                    onClick={handleSearch}
                >
                 🔍
                </button>
            </form>
        </div>
    )
}

export default SearchBar