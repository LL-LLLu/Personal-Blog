import axios from "@/axios";

// Get visitor location statistics (aggregated by location)
export function getVisitorLocationStats() {
    return axios.post("/admin/visitor/location/list")
}

// Get recent visitor logs with pagination
export function getVisitorLogs(current = 1, size = 20) {
    return axios.post(`/admin/visitor/log/list?current=${current}&size=${size}`)
}

// Get visitor statistics summary
export function getVisitorSummary() {
    return axios.post("/admin/visitor/summary")
}
