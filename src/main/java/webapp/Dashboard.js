import React, {useState, useEffect} from "react";
import './Dashboard.css';
import {LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip, ReferenceLine} from "recharts";
import axios from 'axios';

function Dashboard() {
    const [billData, setBillData] = useState([]);
    const [regressionData, setRegressionData] = useState([]);
    const [suggestionsVisible, setSuggestionsVisible] = useState(false);
    const [threshold, setThreshold] = useState('');
    const [savedThreshold, setSavedThreshold] = useState(null);
    const [showThresholdPopup, setShowThresholdPopup] = useState(true);
    const [aiAnswer, setAiAnswer] = useState('');

    useEffect(() => {
        document.title = "ParaPlan"; // <-- whatever you want the tab to say
    }, []);
    const fetchData = async () => {
        try {
            const billResponse = await axios.get('http://localhost:8080/data');
            const regressionResponse = await axios.get('http://localhost:8080/regression');

            setBillData(billResponse.data);

            const {a, b} = regressionResponse.data;
            const regressionPoints = Array.from({length: 30}, (_, i) => ({
                day: i + 1,
                regression: a * (i + 1) + b,
            }));
            setRegressionData(regressionPoints);

            if (regressionResponse.data['day-30'] && savedThreshold !== null) {
                if (regressionResponse.data['day-30'] >= savedThreshold) {
                    const aiResponse = await axios.get('http://localhost:8080/trigger');
                    setAiAnswer(aiResponse.data);
                    setSuggestionsVisible(true);
                } else {
                    setSuggestionsVisible(false);
                }
            } else {
                setSuggestionsVisible(false);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const handleSetThreshold = async () => {
        if (threshold) {
            setSavedThreshold(Number(threshold));
            setShowThresholdPopup(false);

            try {
                await axios.post('http://localhost:8080/threshold', {value: Number(threshold)});
                console.log("Threshold sent successfully");
            } catch (error) {
                console.error("Failed to send threshold:", error);
            }

            fetchData();
        }
    };

    const handleUpdate = () => {
        fetchData();
    };

    const mergedData = Array.from({length: 30}, (_, i) => ({
        day: i + 1,
        total: billData.find(d => d.day === (i + 1))?.total ?? null,
        regression: regressionData.find(d => d.day === (i + 1))?.regression ?? null,
    }));


    return (
        <div className="container">
            {showThresholdPopup && (
                <div className="popup-overlay">
                    <div className="popup">
                        <h2>Enter Threshold</h2>
                        <input
                            type="number"
                            placeholder="Enter threshold (e.g., 1000)"
                            value={threshold}
                            onChange={(e) => setThreshold(e.target.value)}
                            className="threshold-input"
                        />
                        <button onClick={handleSetThreshold} className="set-threshold-button">
                            Set
                        </button>
                    </div>
                </div>
            )}

            {!showThresholdPopup && (
                <>
                    <div className="graphSection">
                        <div className="graphBox">
                            <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
                                <LineChart
                                    width={800}
                                    height={400}
                                    data={mergedData}
                                    margin={{top: 20, right: 20, bottom: 20, left: 20}}
                                >
                                    <XAxis
                                        type="number"
                                        dataKey="day"
                                        domain={[1, 30]}
                                        tickCount={30}
                                        interval={0}
                                        tick={{ fontSize: 14, fill: '#333' }}
                                        label={{
                                            value: 'Day',
                                            position: 'insideBottom',
                                            offset: -5,
                                            style: { fill: '#333', fontSize: 16, fontWeight: 'bold' } // label styling
                                        }}
                                    />

                                    <YAxis
                                        domain={[0, Math.max(500, savedThreshold ?? 0)]}
                                        tick={{ fontSize: 14, fill: '#333' }}
                                        label={{
                                            value: 'Energy Usage (kWh)',
                                            angle: -90,
                                            position: 'insideLeft',
                                            style: { fill: '#333', fontSize: 16, fontWeight: 'bold' }
                                        }}
                                    />
                                    <CartesianGrid stroke="#ccc"/>
                                    <Tooltip/>
                                    {savedThreshold !== null && (
                                        <ReferenceLine
                                            y={savedThreshold}
                                            stroke="red"
                                            strokeDasharray="5 5"
                                            label={{
                                                value: `Threshold (${savedThreshold} kWh)`,
                                                position: 'top',
                                                fill: 'red',
                                                fontSize: 12,
                                                fontWeight: 'bold'
                                            }}
                                        />
                                    )}
                                    <Line type="monotone" dataKey="total" stroke="#8884d8" name="Bill Amount"
                                          connectNulls={false}/>
                                    <Line type="monotone" dataKey="regression" stroke="#82ca9d" name="Trend Line"
                                          dot={false}/>
                                </LineChart>

                                <button className="updateButton" onClick={handleUpdate} style={{marginTop: '20px'}}>
                                    Update
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="suggestionsSection">
                        <h3>Suggestions</h3>
                        <div className="suggestionsTextContainer">
                            {suggestionsVisible ? (
                                <p style={{fontSize: "16px", lineHeight: "1.8", color: "#333", whiteSpace: "pre-line"}}>
                                    {aiAnswer}
                                </p>
                            ) : (
                                <p style={{textAlign: "center", color: "#aaa"}}>No suggestions yet.</p>
                            )}
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}

export default Dashboard;
