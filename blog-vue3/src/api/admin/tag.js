import axios from "@/axios";

// Get tag pagination data
export function getTagPageList(data) {
    return axios.post("/admin/tag/list", data)
}

// Add tag
export function addTag(data) {
    return axios.post("/admin/tag/add", data)
}

// Delete tag
export function deleteTag(id) {
    return axios.post("/admin/tag/delete", {id})
}

// Fuzzy search by tag name
export function searchTags(key) {
    return axios.post("/admin/tag/search", {key})
}

// Get tag select list data
export function getTagSelectList() {
    return axios.post("/admin/tag/select/list")
}