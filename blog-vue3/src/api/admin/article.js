import axios from "@/axios";

// Get article pagination data
export function getArticlePageList(data) {
    return axios.post("/admin/article/list", data)
}

// Delete article
export function deleteArticle(id) {
    return axios.post("/admin/article/delete", {id})
}

// Publish article
export function publishArticle(data) {
    return axios.post("/admin/article/publish", data)
}

// Get article details
export function getArticleDetail(id) {
    return axios.post("/admin/article/detail", {id})
}

// Update article
export function updateArticle(data) {
    return axios.post("/admin/article/update", data)
}