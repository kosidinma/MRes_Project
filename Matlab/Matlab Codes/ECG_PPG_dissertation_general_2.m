%code for finding data peaks by Kosy Onyenso
clear all;
load('ching_mei_all.mat');
Sampling_rate = 1/25;  %0.05s sampling rate
%fid = fopen('ECG.txt', 'r');
% A = ecg(4000:end); %ECG
% A2 = ankle(4000:end); %ankle
% A3 = finger(4000:end); %finger

% 
% A = ecg(1500:end); %ECG
% A2 = ankle(1500:end); %ankle
% A3 = finger(1500:end); %finger


A = ecg; %ECG
A2 = ankle; %ankle
A3 = finger; %finger
[m,~] = size(A);
[m2,~] = size(A2);
num_drops = 0;
window_no = 0;
k = 1;
k2 = 1;
k3 = 1;
for e = 1: 1 : m
%    A(e) = 8000 - A(e); %signal was upside down
%     if (A(e) > 5000)
%         A(e) = 5000;
%     end
%     if (A(e) < 4200) %baseline
%         A(e) = 4200;
%     end
end

pk_place = 1;
pk_dif = 0;
jy = 0;

for i = 6: 1 : m - 6
    if ((A3(i) >= A3(i+1)) &&(A3(i) >= A3(i+2)) &&(A3(i) >= A3(i+3)) && (A3(i) >= A3(i-2))&&(A3(i) >= A3(i-1)) &&(A3(i) > A3(i-3))) 
        num_drops = num_drops+1; 
        if(pk_place == 1)
            fe(pk_place) = i;
            pk_place = pk_place + 1;
        end
        if (pk_place > 1)
            if(i - fe(pk_place-1) < 10)
               pk_dif = 0;
            else
                fe(pk_place) = i;
                pk_dif = fe(pk_place) - fe(pk_place-1);
                pk_place = pk_place + 1;
            end
            jy = pk_dif + jy;
        end
    end
end
ave_peak_dif_s = round(jy/pk_place);
ave_peak_dif = ((jy/pk_place)/2)-1;

% data_increment = round(m/num_drops); %m/num_peaks = number of data per pulse...this function rounds up

% delay = 14; %benny
% % delay = 0; %charence
 delay = -9; %chingmei
%  delay = 12; %frank
 
 start_point = fe(2) ; %start from just before second peak
 
for g = 2 : 1 : pk_place-1 
    if (delay > 0)
        Data_start = (fe(g) - ave_peak_dif) + delay; %get starting point of next data
        Data_end = (fe(g) + ave_peak_dif) + delay; %ending point of data
        next = round(ave_peak_dif/2);
        Data_start2 = (fe(g) - ave_peak_dif); %get starting point of next data
        Data_end2 = (fe(g) + ave_peak_dif); %ending point of data
        next2 = round(ave_peak_dif/2);
        if(Data_start > m)
            break;
        end
        if(Data_end>m)
            Data_end = m;
        end
        
        if(Data_start2 > m)
            break;
        end
        if(Data_end2>m)
            Data_end2 = m-delay;
        end
    else
        Data_start = (fe(g) - ave_peak_dif); %get starting point of next data
        Data_end = (fe(g) + ave_peak_dif); %ending point of data
        next = round(ave_peak_dif/2);
        Data_start2 = (fe(g) - ave_peak_dif) - delay; %get starting point of next data
        Data_end2 = (fe(g) + ave_peak_dif) - delay; %ending point of data
        next2 = round(ave_peak_dif/2);
        if(Data_start > m)
            break;
        end
        if(Data_end>m)
            Data_end = m+delay;
        end
        if(Data_start2 > m)
            break;
        end
        if(Data_end2>m)
            Data_end2 = m;
        end
    end
        
    %%%ECG SIGNAL PROCESSING
    [Peak(window_no+1),Timee(window_no+1)] = max(A(Data_start:Data_end)); %get peak and corresponding time value in window
    Time(window_no+1) = Timee(window_no+1) + Data_start - 1; %correct time to cover all data
    Peak_Time(window_no+1) = Sampling_rate * Time(window_no+1); %correct time with sampling rate

    %%% PPG ANKLE PROCESSING
    [Peak2(window_no+1),Time2(window_no+1)] = min(A2(Data_start2:Data_end2)); %get peak and corresponding time value in window
    Time2(window_no+1) = Time2(window_no+1) + Data_start2 - 1; %correct time to cover all data
    Peak_Time2(window_no+1) = Sampling_rate * Time2(window_no+1); %correct time with sampling rate
    
    %%% PPG FINGER PROCESSING
    [Peak3(window_no+1),Time3(window_no+1)] = min(A3(Data_start:Data_start+Timee(window_no+1))); %get peak and corresponding time value in window
    Time3(window_no+1) = Time3(window_no+1) + Data_start - 1; %correct time to cover all data
    Peak_Time3(window_no+1) = Sampling_rate * Time3(window_no+1); %correct time with sampling rate
    
    window_no = window_no + 1; %increment window number
    
end



for i = 2: 1 : window_no
        PAT_ankle(i) = (Peak_Time2(i) - Peak_Time(i));
        PAT_finger(i) = (Peak_Time3(i) - Peak_Time(i));  
end

if (mean(PAT_ankle) < 0)
    for i = 2: 1 : window_no
        PAT_ankle(i) = abs(Peak_Time2(i) - Peak_Time(i-1));
    end
else
    for i = 2: 1 : window_no
        PAT_ankle(i) = abs(Peak_Time2(i) - Peak_Time(i));
    end
end

if (mean(PAT_finger) < 0)
    for i = 2: 1 : window_no
        PAT_finger(i) = abs(Peak_Time3(i) - Peak_Time(i-1));
    end
else
    for i = 1: 1 : window_no
        PAT_finger(i) = abs(Peak_Time3(i) - Peak_Time(i));
    end
end



for l = 1: 1 : m
    ve(l) = l * Sampling_rate;
end


yu = 0;
[~,re] = size(PAT_ankle);
incre = round(ave_peak_dif/3);
for T = 1: incre : re
    if (T+(incre) >re)
        break;
    end
    yu = yu + 1; 
    v(yu) = mean(PAT_ankle(T:T+incre));
    v_stdev(yu) = std(PAT_ankle(T:T+incre));
    v2(yu) = mean(PAT_finger(T:T+incre));
    v2_stdev(yu) = std(PAT_finger(T:T+incre));
end

figure(1)
subplot(4,1,1);
% plot(Peak_Time,PAT_ankle);
% plot(ve+10,A2);
plot(ve,A2);
% plot(PAT_ankle);
% plot(v);
% errorbar(v,v_stdev^2);
% axis([25 190 -inf 7000])
axis([-inf 220 1000 7000])
% axis([10 210 1000 2700])
% xlabel('Time(s)') % x-axis label
% ylabel('PPG Signal') % y-axis label
title('ANKLE PPG SIGNAL')
subplot(4,1,2);
% plot(Peak_Time,PAT_finger);
% plot(PAT_finger);
p = polyfit(Peak_Time2,PAT_ankle,30);
y1 = polyval(p,Peak_Time2);
plot(Peak_Time2,y1);
% plot(Peak_Time+7,y1);
% plot(v2);
% axis([10 210 0.3 0.7])
% axis([25 190 -inf 0.75])
axis([-inf inf -inf inf])
% xlabel('Time(s)') % x-axis label
% ylabel('PPG Signal') % y-axis label
 title('ANKLE PAT CURVE OF BEST FIT')
% subplot(3,1,3);
% plot(A2);
% axis([-inf inf -inf inf])
% xlabel('Time(s)') % x-axis label
% ylabel('ECG Signal') % y-axis label
% title('ECG Signal')
subplot(4,1,3);
% plot(PAT_finger);
plot(ve,A);
% plot(v2);
%errorbar(v2,v2_stdev);
% axis([25 190 -inf 9200])
axis([-inf 220 -inf inf])
title('FINGER PPG SIGNAL')
subplot(4,1,4);
p2 = polyfit(Peak_Time3,PAT_finger,30);
y2 = polyval(p2,Peak_Time3);
plot(Peak_Time3,y2);
% axis([10 210 -inf 0.4])
% axis([15 190 -inf 0.35])
axis([-inf inf -inf inf])
title('FINGER PAT CURVE OF BEST FIT')

% figure(2)
% hold all
% plot(A);
% plot(A3);
% figure(1)
% subplot(3,1,1);
% plot(ve,A2);
% axis([-inf inf -inf 7000])
% % axis([-inf inf 1000 2700])
% % axis([10 210 1000 2700])
% xlabel('Time(s)') % x-axis label
% ylabel('PPG Signal') % y-axis label
% title('ANKLE PPG SIGNAL')
% subplot(3,1,2);
% plot(ve,A3);
% % axis([10 210 0.3 0.7])
% axis([-inf inf -inf 9200])
% title('FINGER PPG SIGNAL')
% xlabel('Time(s)') % x-axis label
% ylabel('PPG Signal') % y-axis label
% subplot(3,1,3);
% plot(ve,8000-A);
% axis([-inf inf 3000 6000])
% xlabel('Time(s)') % x-axis label
% ylabel('ECG Signal') % y-axis label
% % axis([10 210 3700 inf])
% title('ECG SIGNAL')


% start = 204;
% stop = 230;
% 
%  val_ankle_mean = mean(y1(start:stop))
%  val_ankle_stdev= std(y1(start:stop))
%  val_finger_mean = mean(y2(start:stop))
%  val_finger_stdev= std(y2(start:stop))


% 
 figure(2)
%  subplot(2,1,1);
plot(Peak_Time2,Peak2, 'x-',ve,A2);
% %  errorbar(v2,v2_stdev);
%  axis([-inf inf -inf inf])
% %  xlabel('Time(s)') % x-axis label
% %  ylabel('PAT') % y-axis label
% %  title('PAT ANKLE CURVE')
%  subplot(2,1,2);
% %  plot(peak_time(1:o) * Sampling_rate,PAT_finger);
% plot(A3);
% axis([-inf inf -inf inf])
%  xlabel('Time(s)') % x-axis label
%  ylabel('PAT') % y-axis label
%  title('PAT FINGER CURVE')
%  
 
%  val_ankle_pre60_mean = mean(PAT_ankle(1:75))
%  val1_ankle_pre60_stdev= std(PAT_ankle(1:75))
%  val_ankle_60_mean = mean(PAT_ankle(75:110))
%  val1_ankle_60_stdev= std(PAT_ankle(75:110))
%  val_ankle_post60_mean = mean(PAT_ankle(110:220))
%  val1_ankle_post60_stdev= std(PAT_ankle(110:220))
%  val_ankle_80_mean = mean(PAT_ankle(220:275))
%  val1_ankle_80_stdev= std(PAT_ankle(220:275))
%  val_ankle_post80_mean = mean(PAT_ankle(275:370))
%  val1_ankle_post80_stdev= std(PAT_ankle(275:370))
%  val_ankle_100_mean = mean(PAT_ankle(370:425))
%  val1_ankle_100_stdev= std(PAT_ankle(370:425))
%  val_ankle_post100_mean = mean(PAT_ankle(425:end))
%  val1_ankle_post100_stdev= std(PAT_ankle(425:end))
%  
%  val_finger_pre60_mean = mean(PAT_finger(1:75))
%  val_finger_pre60_stdev= std(PAT_finger(1:75))
%  val_finger_60_mean = mean(PAT_finger(75:110))
%  val_finger_60_stdev= std(PAT_finger(75:110))
%  val_finger_post60_mean = mean(PAT_finger(110:220))
%  val_finger_post60_stdev= std(PAT_finger(110:220))
%  val_finger_80_mean = mean(PAT_finger(220:275))
%  val_finger_80_stdev= std(PAT_finger(220:275))
%  val_finger_post80_mean = mean(PAT_finger(275:370))
%  val_finger_post80_stdev= std(PAT_finger(275:370))
%  val_finger_100_mean = mean(PAT_finger(370:425))
%  val_finger_100_stdev= std(PAT_finger(370:425))
%  val_finger_post100_mean = mean(PAT_finger(425:end))
%  val_finger_post100_stdev= std(PAT_finger(425:end))