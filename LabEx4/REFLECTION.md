# LabEx4 – Room Reflection Questions

**Is Room a replacement for SQLite, or an abstraction over SQLite?**
It's an abstraction. The data still lives in a SQLite database file; Room just writes the boilerplate for us and checks our SQL at compile time.

**What is the role of @Entity?**
It turns a Kotlin class into a table. Each property becomes a column and each object is one row. Here `Customer` becomes the `customers` table.

**What is the role of a DAO?**
The DAO lists every database operation the app is allowed to do (insert, update, delete, the SELECT queries). The rest of the app goes through it instead of writing SQL everywhere.

**Why are insert and delete declared as suspend functions?**
Database writes are slow-ish I/O and Room won't let them run on the main thread. Making them `suspend` lets us call them from a coroutine (`viewModelScope.launch`) so the UI doesn't freeze.

**Why can a SELECT query return Flow<List<Customer>>?**
Room watches the tables a query reads from. When one of them changes, it re-runs the query and emits the new list through the Flow.

**Why is Cursor no longer visible in the application code?**
Room's generated code still uses a Cursor internally, but it reads each row and maps it into a `Customer` object for us.

**Why does the list update without a View All button?**
The UI collects the Flow from `getAllCustomers()`. Any insert, update or delete makes Room emit a fresh list, Compose gets new state, and the LazyColumn recomposes on its own.

**What responsibility does CustomerViewModel have?**
It sits between the UI and the DAO. It exposes the customer list (and search results) as Flows and launches the database writes in `viewModelScope`, so the composables only handle display and user input. It also survives rotation.

**What advantages does Room provide over SQLiteOpenHelper?**
Less code, SQL checked at compile time, automatic row-to-object mapping, Boolean mapping, built-in Flow and coroutine support, and a cleaner split between UI and data code.

**What did the direct SQLite version teach that is hidden by Room?**
How the database actually works underneath: writing CREATE TABLE by hand, ContentValues, moving through a Cursor, storing booleans as 0/1, and refreshing the list yourself after every change.

## Challenges completed
- **Search** – search field with Search and Clear buttons. The ViewModel holds the query in a `MutableStateFlow` and switches to `searchCustomers()` with `flatMapLatest`. Results show in the same `CustomerList`.
- **Update** – `@Update` in the DAO, `updateCustomer()` in the ViewModel. Tapping **Edit** fills the form with that customer, and **Save Changes** saves it with the same id.
