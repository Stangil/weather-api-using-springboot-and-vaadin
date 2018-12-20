package com.stangil44.weatherapp.WeatherApp.view;

import com.stangil44.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.ws.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringUI(path = "")
public class MainView extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeatherButton;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunRiseLabel;
    private Label sunSetLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashBoardMain;
    private VerticalLayout descriptionLayout;
    private HorizontalLayout mainDescriptionLayout;
    private VerticalLayout descriptionLayout2;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashBoardTitle();
        showWeatherButton.addClickListener(clickEvent -> {
           if(!cityTextField.getValue().equals("")) {
               try {
                   updateUI();
                   dashBoardDescription();
               } catch (JSONException e) {
                   Notification.show("Please enter a valid location");
                   cityTextField.setValue("");
                   e.printStackTrace();
               }
           }else Notification.show("Please enter a city");
        });
    }
    private void setUpLayout(){
        mainDescriptionLayout = new HorizontalLayout();
        descriptionLayout = new VerticalLayout();
        weatherMin = new Label();
        weatherMax = new Label();
        descriptionLayout2 = new VerticalLayout();
        pressureLabel = new Label();
        humidityLabel = new Label();
        windSpeedLabel = new Label();
        sunRiseLabel = new Label();
        sunSetLabel = new Label();
        weatherDescription = new Label();
        iconImage = new Image();
        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(mainLayout);
    }
    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        headerLayout.addComponents(title);
        mainLayout.addComponents(headerLayout);
    }
    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image icon = new Image(null, new ClassResource("/stormy-weather.png"));
        icon.setWidth("125px");
        icon.setHeight("125px");
        logoLayout.addComponents(icon);
        mainLayout.addComponent(logoLayout);
    }
    private void setUpForm(){
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);
        //Create the selection component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("120px");
        ArrayList<String> items = new ArrayList<>();
        items.add("Celsius");
        items.add("Fahrenheit");
        unitSelect.setItems(items);
        unitSelect.setValue(items.get(1));

        formLayout.addComponents(unitSelect);

        //Add text field
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponents(cityTextField);

        //Add Button
        showWeatherButton = new Button();
        showWeatherButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(showWeatherButton);

        mainLayout.addComponents(formLayout);
    }
    private void dashBoardTitle() {
        dashBoardMain = new HorizontalLayout();
        dashBoardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        currentLocationTitle = new Label("No city");
        currentLocationTitle.addStyleName(ValoTheme.LABEL_H2);
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);
        //Current Temp Label
        currentTemp = new Label("19F");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);
    }
    private void dashBoardDescription() {
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        //Description Vertical Layout
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        descriptionLayout.addComponents(weatherDescription);
        descriptionLayout.addComponents(weatherMin);
        descriptionLayout.addComponents(weatherMax);
        //Pressure Humidity etc...
        descriptionLayout2.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        descriptionLayout2.addComponents(pressureLabel);
        descriptionLayout2.addComponents(humidityLabel);
        descriptionLayout2.addComponents(windSpeedLabel);
        descriptionLayout2.addComponents(sunRiseLabel);
        descriptionLayout2.addComponents(sunSetLabel);
    }
    private void updateUI() throws JSONException {
        String cityName = cityTextField.getValue().trim();
        String defaultUnit;
        String unit;
        String wsUnit;

        if(unitSelect.getValue().equals("Fahrenheit")){
            defaultUnit = "imperial";
            unit = "\u00b0" + " F";
            wsUnit = " mph";
        }else {
            defaultUnit = "metric";
            unit = "\u00b0" + " C";
            wsUnit = " m/s";
        }

        currentLocationTitle.setValue("Currently in " + cityName);
        JSONObject mainWeatherObject = weatherService.returnMainWeather(cityName, defaultUnit);
        JSONObject windObject = weatherService.returnWind(cityName, defaultUnit);
        JSONObject sunObject = weatherService.returnSys(cityName, defaultUnit);
        double temp = mainWeatherObject.getDouble("temp");
        double minTemp = mainWeatherObject.getDouble("temp_min");
        double maxTemp = mainWeatherObject.getDouble("temp_max");
        double pressure = mainWeatherObject.getDouble("pressure");
        double humidity = mainWeatherObject.getDouble("humidity");
        double windSpeed = windObject.getDouble("speed");
        long sunrise = sunObject.getLong("sunrise") * 1000;//convert to time
        long sunset = sunObject.getLong("sunset") * 1000;

        currentTemp.setValue(temp + unit);
        weatherMin.setValue("Min: " + minTemp+ unit);
        weatherMax.setValue("Max: " + maxTemp+ unit);
        pressureLabel.setValue("Pressure: " + pressure+ "hpa");
        humidityLabel.setValue("Humidity: "+humidity+"%");
        windSpeedLabel.setValue("Wind Speed: " +windSpeed+ wsUnit);
        sunRiseLabel.setValue("Sunrise: "+ convertTime(sunrise));
        sunSetLabel.setValue("Sunset: "+ convertTime(sunset));
        //Get min max pressure humidity

        //Setup icon image
        String iconCode = "";
        String description = "";
        JSONArray jsonArray = weatherService.returnWeatherArray(cityName, defaultUnit);

        for(int i = 0 ; i < jsonArray.length() ; i++){
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            description = weatherObject.getString("description");
            iconCode = weatherObject.getString("icon");
            System.out.println("iconCode "+iconCode);
            }
        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/w/"+iconCode+".png"));

        dashBoardMain.addComponents(currentLocationTitle,iconImage,currentTemp);
        mainLayout.addComponents(dashBoardMain);
        //Update description UI

        weatherDescription.setValue("Weather: "+description);

        mainDescriptionLayout.addComponents(descriptionLayout,descriptionLayout2);
        mainLayout.addComponents(mainDescriptionLayout);
    }
    private String convertTime (long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat( "hh.mm aa");
        return  dateFormat.format(new Date(time));
    }
}
