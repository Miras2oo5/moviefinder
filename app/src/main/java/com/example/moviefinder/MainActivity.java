package com.example.moviefinder;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviefinder.data.api.ApiClient;
import com.example.moviefinder.data.api.MovieApiService;
import com.example.moviefinder.data.model.Movie;
import com.example.moviefinder.data.model.MovieResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private static final String API_KEY = "854c5f73dfc2671b1dea661201afacac"; // сюда вставь свой API-ключ TMDb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Обработчик SearchView для поиска
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchMovies(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchMovies("popular");  // Загружаем популярные фильмы, если поиск пустой
                }
                return false;
            }
        });

        // Загружаем популярные фильмы по умолчанию
        fetchMovies("popular");
    }

    private void fetchMovies(String query) {
        MovieApiService apiService = ApiClient.getClient().create(MovieApiService.class);
        Call<MovieResponse> call;

        // Если запрос пустой, загружаем популярные фильмы
        if (query.equals("popular")) {
            call = apiService.getPopularMovies(API_KEY, "en-US", 1);  // Загружаем популярные фильмы
        } else {
            // Если есть текст в поиске, ищем фильмы по запросу
            call = apiService.searchMovies(API_KEY, "en-US", query, 1);  // Поиск фильмов по запросу
        }

        // Выполняем запрос
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    adapter = new MovieAdapter(movies);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
