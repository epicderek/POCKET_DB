# POCKET_DB
The Data Types and their Uploader and Downloader from the SQL Database of POCKET.

All data types and their handlers responsible for their storage in the database are listed in the POCKET_DB package.

A Place is a comprehensive representation of a geographical position in terms of a latitude and a longitude, from which the relevant information such as the address, the city, etc. are derived. Places of different associated information but identical coordinates are stored as one Place object.

A Place may be created this way.

Place pla = new Place(doubel lati, double long, String name);

The third argument is an optional name denoting this place.

A PlaceHandler takes care of both the storage of native place objects in the database and the retrieve and reconstruction of stored Place data back to a Place object.
