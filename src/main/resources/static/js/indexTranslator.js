function translateIndexHtml() {
    $("#generator-btn").text(i18n("generator"));
    $("#scenario-dropdown1").text(i18n("scenario"));
    $("#scenario-dropdown2").text(i18n("scenario"));
    $("#config-dropdown").text(i18n("configuration"));
    $("#config-dropdown2").text(i18n("configuration"));
    $("#discardScenario").text(i18n("discardScenario"));
    $("#configurationManagement").text(i18n("configManagement"));
    $("#curConfigTxt").html("| " + i18n("currentConfig") + ":&nbsp;");
    $("#filterHeader").text(i18n("filter") + ":");
    $("#labeloption1").text(i18n("relative"));
    $("#labeloption2").text(i18n("absolute"));
    $("#selectedPhases").text(i18n("selectedPhases") + ":");
    $("#speedTxt").text(i18n("speed")+ ":");
    $("#bp-btn-phaseselect").prop("title", i18n("selectPhases") + "...");
    $("#bp-createeventbtn").text(i18n("createEvent"));
    $("#bp-phasebtn").text(i18n("phaseManagement"));
    $("#bp-startpausebtn").text(i18n("startSimulation"));
    $("#bp-stopbtn").text(i18n("stop"));

    //Enable tooltips
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    });
}