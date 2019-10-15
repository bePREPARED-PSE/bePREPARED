# README

*bePREPARED* is a software used to simulate events of a crises situation. For detailed information on **how to use**
*bePREPARED*, check out [this user guide](doc/userguide.md). If you are a **developer** and you want to get an insight
in how *bePREPARED* works and how to extend its functionality, check out the [developer guide](doc/developerguide.md).

---

## Quick start guide

1. Simply download all the [required javascript libraries](doc/requiredJSlibs.md) and put them in
   `src/main/resources/static/lib`.
2. Run Maven with the goal `package` (or higher).
3. Launch the application by executing the jar file left by Maven:
   ```
   java -jar target/beprepared.jar
   ```

   **NOTE:** The default port is set to 8080.
4. Open the webpage in your favourite internet browser.
