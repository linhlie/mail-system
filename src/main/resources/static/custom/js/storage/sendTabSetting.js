function setCachedSeparateTabSetting(value) {
    localStorage.setItem("separateTab", value);
}

function getCachedSeparateTabSetting() {
    var cachedSeparateTabStr = localStorage.getItem("separateTab");
    var cachedSeparateTab = typeof cachedSeparateTabStr !== "string" ? false : !!JSON.parse(cachedSeparateTabStr);
    return cachedSeparateTab;
}
