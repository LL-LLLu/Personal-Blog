import axios from "@/axios";

// Get comment page list
export function getCommentPageList(data) {
    return axios.post("/admin/comment/list", data)
}

// Delete comment
export function deleteComment(id) {
    return axios.post("/admin/comment/delete", {id})
}

// Examine comment
export function examineComment(data) {
    return axios.post("/admin/comment/examine", data)
}
