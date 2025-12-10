import axios from "@/axios";

// Get wiki pagination data
export function getWikiPageList(data) {
    return axios.post("/admin/wiki/list", data)
}

// Add wiki
export function addWiki(data) {
    return axios.post("/admin/wiki/add", data)
}

// Update wiki isTop
export function updateWikiIsTop(data) {
    return axios.post("/admin/wiki/isTop/update", data)
}

// Update wiki isPublish
export function updateWikiIsPublish(data) {
    return axios.post("/admin/wiki/isPublish/update", data)
}

// Delete wiki
export function deleteWiki(id) {
    return axios.post("/admin/wiki/delete", {id})
}

// Update wiki
export function updateWiki(data) {
    return axios.post("/admin/wiki/update", data)
}

// Get wiki catalogs
export function getWikiCatalogs(id) {
    return axios.post("/admin/wiki/catalog/list", {id})
}

// Update wiki catalogs
export function updateWikiCatalogs(data) {
    return axios.post("/admin/wiki/catalog/update", data)
}