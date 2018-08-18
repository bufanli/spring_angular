# Preparation
1. install nodejs.
2. install angular cli by `npm install -g @angular/cli`
3. install eclipse or intelliJ for SpringBoot project.
4. install vs code for angular project.
5. install plugins to vs code.
   * Debugger for Chrome
   * Angular 2,4 and upcoming latest TypeScript HTML Snippets
   * Angular 6 Snippets

# build angular
1. go to angular folder.
2. run `npm install` to download dependency modules.
3. run `ng build`, then js/css/html files will be 
   generated into folder of ./spring/src/main/resources/static

# build spring 
1. import spring project into your eclipse or intelliJ.
2. run the main class, `DemoApplication.java`.
3. open browser to navigate `http://localhost:8080/`

# debug angular
1. go to angular folder.
2. run `ng serve` to start angular project.
3. start vs code and open angular folder.
4. set breakpoints and run `Chrome` in debug view.

# debug spring
1. debug spring project in eclipse or intelliJ.
    * In inelliJ, when importing spring project(maven project), it is possible that angular
      project is also imported, in that case, go to `project structure` menu, in sources tab, delete angular
      project.(our angular project will be developed in `visual stdio code`.)
