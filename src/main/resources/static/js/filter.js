function gotoApplication(index) {
    console.log(index);

    url = "/application/{id}(id=" + index + ")";

    let param = {};
    param["id"] = index;
    console.log(param);
    $('#appList').load(url, param);
}

const listLength = document.getElementById("listLength").textContent;
const clearButton = document.getElementById("clearFilter");
const filterButtonL = document.getElementById("filterAllL");
const filterButtonM = document.getElementById("filterAllM");
const filterButtonS = document.getElementById("filterAllS");
clearButton.style.display = "none";
filterButtonL.style.display = "none";
filterButtonM.style.display = "none";
filterButtonS.style.display = "none";

function showClearFilterBtn() {
    clearButton.style.display = "";
}
function showFilterBtnL() {
    filterButtonL.style.display = "block";
    filterButtonM.style.display = "none";
    filterButtonS.style.display = "none";
}
function showFilterBtnM() {
    filterButtonM.style.display = "block";
    filterButtonL.style.display = "none";
    filterButtonS.style.display = "none";
}
function showFilterBtnS() {
    filterButtonS.style.display = "block";
    filterButtonL.style.display = "none";
    filterButtonM.style.display = "none";
}

function filterNameL() {
    filterName("nameL")
}
function filterNameM() {
    filterName("nameM")
}
function filterNameS() {
    filterName("nameS")
}
function filterName(elementId) {
    console.log("FilterName");
    let filter, i;
    filter = document.getElementById(elementId).value.toLowerCase();
    console.log(filter);
    console.log(listLength);
    for (i = 0; i < listLength; i++) {
        filterNameRow(i, filter)
    }
}
function filterNameRow(index, filter) {
    let appRow, td, name;
    console.log(index);
    appRow = document.getElementById("applicationRow" + index);
    td = appRow.getElementsByTagName("td");
    name = td[0].getElementsByTagName("a")[0].textContent;
    console.log(name);
    if (name.toLowerCase().indexOf(filter) > -1) {
        appRow.style.display = "";
    } else {
        appRow.style.display = "none";
    }
}

function filterDateFromL() {
    filterDateFrom("dateFromL")
}
function filterDateFromM() {
    filterDateFrom("dateFromM")
}
function filterDateFromS() {
    filterDateFrom("dateFromS")
}
function filterDateFrom(elementId) {
    console.log("FilterDateFrom");
    let filter, i;
    filter = document.getElementById(elementId).value;
    console.log(filter);
    console.log(listLength);
    for (i = 0; i < listLength; i++) {
        filterDateFromRow(i, filter)
    }
}
function filterDateFromRow(index, filter) {
    let appRow, td, dateFrom;
    console.log(index);
    appRow = document.getElementById("applicationRow" + index);
    td = appRow.getElementsByTagName("td");
    dateFrom = td[3].getElementsByTagName("div")[0].textContent;
    console.log(dateFrom);
    if (new Date(dateFrom) >= new Date(filter)) {
        appRow.style.display = "";
    } else {
        appRow.style.display = "none";
    }
}

function filterDateToL() {
    filterDateTo("dateToL")
}
function filterDateToM() {
    filterDateTo("dateToM")
}
function filterDateToS() {
    filterDateTo("dateToS")
}
function filterDateTo(elementId) {
    console.log("FilterDateTo");
    let filter, i;
    filter = document.getElementById(elementId).value;
    console.log(filter);
    console.log(listLength);
    for (i = 0; i < listLength; i++) {
        filterDateToRow(i, filter)
    }
}
function filterDateToRow(index, filter) {
    let appRow, td, dateTo;
    console.log(index);
    appRow = document.getElementById("applicationRow" + index);
    td = appRow.getElementsByTagName("td");
    dateTo = td[4].getElementsByTagName("div")[0].textContent;
    console.log(dateTo);
    if (new Date(dateTo) <= new Date(filter)) {
        appRow.style.display = "";
    } else {
        appRow.style.display = "none";
    }
}


function filterCompetenceL() {
    filterCompetence("competenceL")
}
function filterCompetenceM() {
    filterCompetence("competenceM")
}
function filterCompetenceS() {
    filterCompetence("competenceS")
}
function filterCompetence(elementId) {
    console.log("FilterCompetence");
    let filter, i;
    filter = document.getElementById(elementId).value;
    console.log(filter);
    console.log(listLength);
    for (i = 0; i < listLength; i++) {
        filterCompetenceRow(i, filter)
    }
}
function filterCompetenceRow(index, filter) {
    let appRow, td, competenceList, competence, i, count = 0;
    console.log(index);
    appRow = document.getElementById("applicationRow" + index);

    td = appRow.getElementsByTagName("td");
    competenceList = td[1].getElementsByTagName("div");
    if (filter === "alla") {
        appRow.style.display = "";
    } else {
        for (i = 0; i < competenceList.length; i++) {
            competence = competenceList[i].textContent;
            console.log(competence);
            if (competence === filter) {
                count = count + 1;
                console.log("count");
                console.log(count);
            }
        }
        console.log("final count: " + count);
        if (count === 0) {
            appRow.style.display = "none";
        } else {
            appRow.style.display = "";
        }
    }
}

function clearFilter() {
    let i, appRow;
    console.log("clearFilter");
    console.log(listLength);
    document.getElementById("dateToL").value = "";
    document.getElementById("dateFromL").value = "";
    document.getElementById("nameL").value = "";
    document.getElementById("competenceL").value = "alla";
    document.getElementById("dateToM").value = "";
    document.getElementById("dateFromM").value = "";
    document.getElementById("nameM").value = "";
    document.getElementById("competenceM").value = "alla";
    document.getElementById("dateToS").value = "";
    document.getElementById("dateFromS").value = "";
    document.getElementById("nameS").value = "";
    document.getElementById("competenceS").value = "alla";
    for (i = 0; i < listLength; i++) {
        appRow = document.getElementById("applicationRow" + i);
        appRow.style.display = "";
    }
    clearButton.style.display = "none";
    filterButtonL.style.display = "none";
    filterButtonM.style.display = "none";
    filterButtonS.style.display = "none";
}