const API = {
    base: "",

    request: async (url, options = {}) => {
        const response = await fetch(`${API.base}${url}`, {
            headers: {
                "Content-Type": "application/json",
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        if (response.status === 204) return null;
          return response.json();

    },

    get: (url) => API.request(url),

    post: (url, data) => API.request(url, {
        method: "POST",
        body: JSON.stringify(data)
    }),

    put: (url, data) => API.request(url, {
        method: "PUT",
        body: JSON.stringify(data)
    }),

    patch: (url) => API.request(url, {
        method: "PATCH"
    }),

    delete: (url) => API.request(url, {
        method: "DELETE"
    })

};