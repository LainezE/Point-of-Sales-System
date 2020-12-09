
Dependencies:
    This program requires the libraries "AbsoluteLayout.jar" and "mysql-connector-java-8.0.17".


Database Setup:
    The MySQL database table structure/queries is located in the file "Database_Structure.sql".
    The current database data queries are located in the file "Database_Data.sql".


Compiling:
    To create a single jar file that includes the dependent libraries, with netbeans,
        place the library files in the "dist\lib" directory. Then run go to files
        tab and right click "build.xml" and select "run target" and then go to
        "other targets" and select "package-for-store" which will output a stand-alone
        jar file in the "store" directory.


Barcode Scanner:
    The barcode scanner used in this project is the Focus MS1690, which registers as a keyboard to the computer.
    For the barcode scanner to auto select the field to add items to cart, it must be
        setup so that it sends the pre-fix characters "alt + a". The scanner must
        also send the suffix-character for "enter" which will add the item automatically
        after it "types" it.
    The scanner manual for the scanner used in this project is located in the file "MS1690_Focus_Config.pdf".
